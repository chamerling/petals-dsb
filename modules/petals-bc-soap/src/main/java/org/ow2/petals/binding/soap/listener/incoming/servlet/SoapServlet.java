/**
 * PETALS - PETALS Services Platform. Copyright (c) 2005 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.incoming.servlet;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.COMPONENT_CONTEXT_SERVICE_PARAM;
import static org.ow2.petals.binding.soap.SoapConstants.Axis2.CONSUMES_SERVICE_PARAM;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.MessageExchange.Role;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.transport.http.AxisServlet;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;
import org.ow2.petals.binding.soap.listener.incoming.jetty.SOAPHttpTransportListener;
import org.ow2.petals.binding.soap.listener.incoming.jetty.SOAPHttpsTransportListener;
import org.ow2.petals.binding.soap.listener.incoming.jetty.ServerStats;
import org.ow2.petals.binding.soap.util.WsdlHelper;
import org.ow2.petals.commons.PetalsExecutionContext;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.util.LoggingUtil;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;

/**
 * Special servlet for Jetty server. This servlet extends AxisServlet to
 * override the initConfigContext method that load a configuration context from
 * a War file. In the SoapComponent, the configuration context is loaded at
 * component startup and is passed by Jetty server and this servlet.
 * 
 * @author Adrien LOUIS - EBMWebSourcing
 * @author Christophe Hamerling - EBM WebSourcing
 * 
 */
@SuppressWarnings("serial")
public class SoapServlet extends AxisServlet {
	
	private final Logger logger;
	
	private final ConfigurationContext configurationContext;
	
    private transient final ServerStats stats;
    
    private final SoapServerConfig config;

    /**
     * Creates a new instance of SoapServlet
     * 
     */
	public SoapServlet(final Logger logger,
			final ConfigurationContext configurationContext,
			final ServerStats stats, final SoapServerConfig config) {
		this.logger = logger;
		this.configurationContext = configurationContext;
		this.stats = stats;
		this.config = config;
	}

    /**
     * 
     * @param request
     * @return
     */
    private String createServiceName(final HttpServletRequest request) {
        final String remove = request.getContextPath() + request.getServletPath() + "/";
        final String res = request.getRequestURI().replace(remove, "");
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.http.AxisServlet#doGet(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String queryString = request.getQueryString();

        if (isImportWSDLRequest(queryString)) {
            printImportedDocuments(request, response);
        } else {
            this.stats.newGetRequest();
            
            // to close the connection (necessary for HTTPS connection to avoid
            // exception)
            response.addHeader("Connection", "Close");

            super.doGet(request, response);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.http.AxisServlet#doPost(javax.servlet.http
     * .HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        PetalsExecutionContext.initFlowInstanceId();
        PetalsExecutionContext.nextFlowStepId();

        try {
	        this.stats.newPostRequest();
	        
	        // to close the connection (necessary for HTTPS connection to avoid
	        // exception)
	        response.addHeader("Connection", "Close");
	
	        super.doPost(request, response);
		} catch (Throwable t) {
			this.logger.log(Level.WARNING, "Error when handling a HTTP POST request", t);
			LoggingUtil.addMonitFailureTrace(this.logger,
					PetalsExecutionContext.getFlowAttributes(),
					t.getMessage(), Role.CONSUMER);
        }
    }

    /**
     * 
     * @param queryString
     * @return
     */
    private boolean isImportWSDLRequest(final String queryString) {
        return (queryString != null) && (queryString.toLowerCase().startsWith("wsdl"));
    }

    /**
     * Get and prints the imported document from WSDL
     * 
     * @param request
     * @param response
     * @param queryString
     * @throws AxisFault
     * @throws IOException
     * @throws ServletException
     */
    private void printImportedDocuments(final HttpServletRequest request,
            final HttpServletResponse response) throws AxisFault, IOException, ServletException {
        // try to find if the axisService has already been registered
        final AxisService axisService = this.axisConfiguration
                .getService(createServiceName(request));
        if (axisService != null) {
            final List<?> exposedTransports = axisService.getExposedTransports();
            String transport = request.getScheme();
            if (exposedTransports.contains(transport)) {

                final String queryString = request.getQueryString();
                String localServiceAddress = config.buildServiceAddress(transport, axisService.getName());

                Parameter componentContextParam = axisService
                        .getParameter(COMPONENT_CONTEXT_SERVICE_PARAM);
                ComponentContext componentContext = (ComponentContext) componentContextParam
                        .getValue();
                Parameter consumesParam = axisService.getParameter(CONSUMES_SERVICE_PARAM);
                Consumes consumes = (Consumes) consumesParam.getValue();

                if ("wsdl".equals(queryString)) {
                    try {

                        final ServletOutputStream out = response.getOutputStream();
                        WsdlHelper.printWSDL(this.logger, consumes, componentContext, out,
                                localServiceAddress);
                        out.flush();
                        out.close();
                    } catch (final Exception e) {
                        throw new ServletException("Error during streaming serialization", e);
                    }
                } else {
                    final String importsRootURI = localServiceAddress + "?wsdl=";
                    Map<URI, Document> importedDocsMap = WsdlHelper.getImportedDocuments(this.logger,
                            consumes, componentContext, importsRootURI);

                    boolean find = false;
                    for (final Map.Entry<URI, Document> entry : importedDocsMap.entrySet()) {
                        if (entry.getKey().toString().contains(queryString)) {
                            find = true;
                            final Document importedDoc = entry.getValue();
                            final ServletOutputStream out = response.getOutputStream();
                            try {
                                XMLPrettyPrinter.prettify(importedDoc, out,
                                        XMLPrettyPrinter.getEncoding(importedDoc));
                                out.flush();
                                out.close();
                            } catch (final Exception e) {
                                throw new ServletException("Error on " + queryString
                                        + " streaming serialization", e);
                            }
                        }
                    }
                    if (!find) {
                        throw new ServletException("Error: Document unknown: " + queryString
                                + ". Available documents are "
                                + importedDocsMap.keySet().toString());
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.http.AxisServlet#init(javax.servlet.ServletConfig
     * )
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute(AxisServlet.CONFIGURATION_CONTEXT, this.configurationContext);
        super.init(config);
        // dirty hack
        try {
            configureListenerManager();
        } catch (AxisFault e) {
            throw new ServletException(e);
        }
    }

    /**
     * TODO: Call it after init() of AxisServlet (Axis bug) otherwise
     * {@link SOAPHttpsTransportListener} is not set as
     * {@link TransportListener}.
     * 
     * @throws AxisFault
     */
    public void configureListenerManager() throws AxisFault {
        ListenerManager listenerManager = this.configurationContext.getListenerManager();
        final TransportInDescription httpTrsIn = new TransportInDescription(
                Constants.TRANSPORT_HTTP);
        httpTrsIn.setReceiver(new SOAPHttpTransportListener(this.config));
        final TransportInDescription httpsTrsIn = new TransportInDescription(
                Constants.TRANSPORT_HTTPS);
        httpsTrsIn.setReceiver(new SOAPHttpsTransportListener(this.config));
        if (listenerManager == null) {
            listenerManager = new ListenerManager();
            listenerManager.init(this.configurationContext);
        }
        listenerManager.addListener(httpTrsIn, true);
        listenerManager.addListener(httpsTrsIn, true);
    }
}
