/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.kernel.information;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ow2.petals.tools.ws.WebServiceException;
import org.ow2.petals.tools.ws.WebServiceManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class IndexServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -1508536315079398078L;

    private final WebServiceManager webServiceManager;

    /**
     * @param webServiceManager
     * 
     */
    public IndexServlet(WebServiceManager webServiceManager) {
        this.webServiceManager = webServiceManager;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        OutputStream os = response.getOutputStream();
        if (os != null) {
            os
                    .write("<html><header><title>SOA4All :: Distributed Service Bus</title></header><body>"
                            .getBytes());

            os.write("<h2>Information</h2>".getBytes());

            os.write("<h2>Available Container Web Services</h2>".getBytes());
            List<String> services = null;
            try {
                services = this.webServiceManager.getServicesURL();
            } catch (WebServiceException e) {
            }
            if ((services != null) && (services.size() > 0)) {
                os.write("<ul>".getBytes());
                for (String string : services) {
                    os.write(("<li><a href='" + string + "?wsdl'>" + string + "</a></li>")
                            .getBytes());
                }
                os.write("</ul>".getBytes());
            } else {
                os.write("No services".getBytes());
            }

            os.write("<h2>Available Platform Web Services</h2>".getBytes());
            os.write("TODO".getBytes());

            os.write("<h2>Available Platform REST Services</h2>".getBytes());
            os.write("TODO".getBytes());

            os.write("</body></html>".getBytes());
        }

        if (os != null) {
            try {
                os.flush();
            } catch (Exception e) {
            }
            try {
                os.close();
            } catch (Exception e) {
            }
        }

    }

}
