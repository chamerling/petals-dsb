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

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.transport.http.AxisServlet;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.binding.soap.listener.incoming.PetalsAxisService;
import org.ow2.petals.binding.soap.listener.incoming.jetty.ServerStats;
import org.w3c.dom.Document;

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
public class SoapServlet extends AxisServlet {

    // TODO: configContext is reset during Servlet.init by AxisServlet, so an
    // other
    // variable must be used to save the configuration.
    protected transient ConfigurationContext copyOfconfigContext;

    private final ServerStats stats;

    /**
     * 
     */
    private static final long serialVersionUID = -8035921158344750120L;

    /**
     * Creates a new instance of SoapServlet
     * 
     * @param configContext
     * @param stats
     */
    public SoapServlet(final ConfigurationContext configContext, final ServerStats stats) {
        this.copyOfconfigContext = configContext;
        this.stats = stats;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.http.AxisServlet#initConfigContext(javax.servlet
     * .ServletConfig)
     */
    @Override
    protected ConfigurationContext initConfigContext(final ServletConfig config)
            throws ServletException {
        // nothing to do, just return the configuration context. Avoid loading
        // the configuration from default location (WAR)
        this.copyOfconfigContext.setProperty(Constants.CONTAINER_MANAGED, Constants.VALUE_TRUE);
        return this.copyOfconfigContext;
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
        String queryString = request.getQueryString();
        
        if (isImportWSDLRequest(queryString)) {
            this.printImportedDocuments(request, response, queryString);
        } else {
            this.stats.newGetRequest();
            super.doGet(request, response);
        }
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
            final HttpServletResponse response, String queryString) throws AxisFault, IOException,
            ServletException {
        AxisConfiguration axisConfig = this.axisConfiguration;
        // try to find if the axisService has already been registered
        AxisService axisService = axisConfig.getService(createServiceName(request));
        if (axisService != null) {
            if (axisService instanceof PetalsAxisService) {

                String requestURI = ((PetalsAxisService) axisService).getLocalAddress() + "/" + axisService.getName();
                String importsRootURI = requestURI.concat("?wsdl=");
                
                if("wsdl".equals(queryString)){
                    try{
                        
                        final ServletOutputStream out = response.getOutputStream();
                        axisService.printWSDL(out, importsRootURI);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        throw new ServletException("Error during streaming serialization", e);
                    }
                }else{
                    
                    Map<URI, Document> importedDocsMap= null;

                    //FIXME there are other way to request imported document than ...wsdl=..
                    //String docFileName = queryString.replace("wsdl=", "");
                    //docFileName = importsRootURI + docFileName;

                    importedDocsMap = ((PetalsAxisService) axisService).getImportedDocuments(importsRootURI);

                    boolean find = false;
                    
                    for (final Map.Entry<URI, Document> entry : importedDocsMap.entrySet()) {
                        if (entry.getKey().toString().contains(queryString)) {
                            find = true;
                            final Document importedDoc = entry.getValue();
                            final ServletOutputStream out = response.getOutputStream();
                            try{
                                XMLPrettyPrinter.prettify(importedDoc, out, XMLPrettyPrinter
                                        .getEncoding(importedDoc));
                                out.flush();
                                out.close();
                            } catch (final Exception e) {
                                throw new ServletException("Error on " + queryString
                                        + " streaming serialization", e);
                            }
                        }
                    }
                    if (!find) {
                        throw new ServletException("Error: Document unknown: " + queryString+". Available documents are "+importedDocsMap.keySet().toString());
                    }
                }
            }
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
        this.stats.newPostRequest();
        super.doPost(request, response);
    }
    
    /**
     * 
     * @param queryString
     * @return
     */
    private boolean isImportWSDLRequest(String queryString) {
        return (queryString != null) && (queryString.toLowerCase().startsWith("wsdl"));
    }

    /**
     * 
     * @param request
     * @return
     */
    private String createServiceName(HttpServletRequest request) {
        String remove = request.getContextPath() + request.getServletPath() + "/";
        String res = request.getRequestURI().replace(remove, "");
        return res;
    }
}
