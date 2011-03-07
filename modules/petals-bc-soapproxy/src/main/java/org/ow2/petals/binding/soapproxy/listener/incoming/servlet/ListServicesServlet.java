/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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

package org.ow2.petals.binding.soapproxy.listener.incoming.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.ow2.petals.binding.soapproxy.listener.incoming.PetalsAxisService;
import org.ow2.petals.binding.soapproxy.listener.incoming.SoapServerConfig;

/**
 * Servlet used to display the list of services. It replaces the Axis2 one.
 * 
 * @author chamerling - EBM Websourcing
 * 
 */
public class ListServicesServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -4951673084170985493L;

    /**
     * The HTML title string
     */
    private static final String HTML_TOP = "<html><head><title>petals-bc-soap : Services List</title></head><body><h1>PEtALS BC SOAP</h1>";

    private static final String HTML_BOTTOM = "</body></html>";

    public static final String MAPPING_NAME = "listServices";

    /**
     * The axis2 configuration context used to get services list
     */
    private final ConfigurationContext configContext;

    /**
     * The SOAP server configuration
     */
    private final SoapServerConfig soapConfig;

    /**
     * Creates a new instance of ListServicesServlet
     * 
     * @param configurationContext
     * @param active
     */
    public ListServicesServlet(final ConfigurationContext configurationContext,
            final SoapServerConfig soapConfig) {
        this.configContext = configurationContext;
        this.soapConfig = soapConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse)
     */
    @Override
    public void service(final ServletRequest request, final ServletResponse response)
            throws ServletException, IOException {
        final ServletOutputStream out = response.getOutputStream();
        out.write(HTML_TOP.getBytes());

        if (this.soapConfig.isProvidesList()) {
            this.printServicesList(out);
        } else {
            this.listNotAvailable(out);
        }

        out
                .write(("<p><center><a href='" + this.soapConfig.getBaseURL() + "'> - Index -</a></center></p>")
                        .getBytes());
        out.write(HTML_BOTTOM.getBytes());
        out.flush();
        out.close();
    }

    /**
     * Print the list of services with links to their WSDL description
     * 
     * @param out
     * @throws IOException
     */
    private void printServicesList(final ServletOutputStream out) throws IOException {
        final Map<?, ?> services = this.configContext.getAxisConfiguration().getServices();
        final Set<?> keys = services.keySet();

        out.write("<h2>Available services</h2>".getBytes());
        if (keys.size() > 0) {
            for (final Object key : keys) {
                final AxisService service = (AxisService) services.get(key);
                if (!service.getName().startsWith(PetalsAxisService.OUTGOING_SERVICE_CLIENT_PREFIX)) {
                    out.write("<li><a href='".getBytes());
                    out.write(service.getName().getBytes());
                    out.write("?wsdl'>".getBytes());
                    out.write(service.getName().getBytes());
                    out.write("</a></li>".getBytes());
                }
            }
        } else {
            out.write("No service".getBytes());
        }
    }

    /**
     * The list has not been activated in component
     * 
     * @param out
     * @throws IOException
     */
    private void listNotAvailable(final ServletOutputStream out) throws IOException {
        out.write(ListServicesServlet.HTML_TOP.getBytes());
        out.write("<h1><font color='red'>The list of services is not available</font></h1>"
                .getBytes());
        out.write("It must be activated in the SOAP component descriptor...".getBytes());
        out.write("</body></html>".getBytes());
        out.flush();
        out.close();
    }

}
