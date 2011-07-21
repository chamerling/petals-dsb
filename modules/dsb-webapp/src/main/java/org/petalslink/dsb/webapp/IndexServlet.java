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
package org.petalslink.dsb.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.petalslink.dsb.webapp.api.DSBManagement;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class IndexServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -1660031848981077801L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        DSBManagement dsbManagement = DSBManagementSingleton.get();

        OutputStream os = resp.getOutputStream();
        if (os != null) {
            os.write("<html><header><title>Distributed Service Bus</title></header><body>"
                    .getBytes());

            if (dsbManagement != null) {
                os.write("<h2>Container Information</h2>".getBytes());
                os.write(dsbManagement.getContainerInfo().getBytes());

                os.write("<h2>Available Container Web Services</h2>".getBytes());
                Set<String> list = null;
                list = dsbManagement.getContainerServices();
                if ((list != null) && (list.size() > 0)) {
                    os.write("<ol>".getBytes());
                    for (String string : list) {
                        os.write(("<li><a href='" + string + "?wsdl' target='_blank'>" + string + "</a></li>")
                                .getBytes());
                    }
                    os.write("</ol>".getBytes());
                } else {
                    os.write("No services".getBytes());
                }

                os.write("<h2>Available Web Services</h2>".getBytes());
                Set<String> set = dsbManagement.getWebServices();
                if ((set != null) && (set.size() > 0)) {
                    os.write("<ol>".getBytes());
                    for (String string : set) {
                        os.write(("<li><a href='" + string + "?wsdl' target='_blank'>" + string + "</a></li>")
                                .getBytes());
                    }
                    os.write("</ol>".getBytes());
                } else {
                    os.write("No services".getBytes());
                }

                os.write("<h2>Available REST Services</h2>".getBytes());
                set = dsbManagement.getRESTServices();
                if ((set != null) && (set.size() > 0)) {
                    os.write("<ol>".getBytes());
                    for (String string : set) {
                        os.write(("<li><a href='" + string + "' target='_blank'>" + string + "</a></li>")
                                .getBytes());
                    }
                    os.write("</ol>".getBytes());
                } else {
                    os.write("No services".getBytes());
                }
            } else {
                os.write("<b>No information provided (DSB Management has not been injected...)</b>".getBytes());
            }
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
