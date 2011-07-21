/**
 * 
 */
package org.petalslink.dsb.kernel.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chamerling
 * 
 */
public class ListServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Set<String> webapps;

    /**
     * 
     */
    public ListServlet(Set<String> webapps) {
        this.webapps = webapps;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        OutputStream os = resp.getOutputStream();
        if (os != null) {

            os.write("<html><header><title>Distributed Service Bus</title></header><body><h2>DSB WebApps</h2>"
                    .getBytes());

            if (webapps != null) {
                os.write("<ul>".getBytes());
                for (String webapp : webapps) {
                    os.write(("<li><a href='../" + webapp + "/' target='_blank'>" + webapp + "</a></li>")
                            .getBytes());
                }
                os.write("</ul>".getBytes());
            } else {
                os.write("<b>No webapps</b>".getBytes());
            }

            os.write("</body></html>".getBytes());
            // flush...
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
