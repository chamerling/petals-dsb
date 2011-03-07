package org.petalslink.dsb.webapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.petalslink.dsb.webapp.api.DSBManagement;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class DSBInitServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -2755241234770762362L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        Object o = this.getServletContext().getAttribute("dsbmanagement");
        if (o != null) {
            DSBManagement dsbManagement = null;
            try {
                dsbManagement = (DSBManagement) o;
            } catch (RuntimeException e1) {
            }
            DSBManagementSingleton.store(dsbManagement);
        } else {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // got a request, do nothing...
        System.err.println("Got a request, do nothing on the DSB init servlet...");
    }

}
