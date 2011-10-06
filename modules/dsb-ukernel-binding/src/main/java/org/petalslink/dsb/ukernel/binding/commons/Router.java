/**
 * 
 */
package org.petalslink.dsb.ukernel.binding.commons;

import java.util.ArrayList;
import java.util.List;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.ukernel.binding.Route;

/**
 * @author chamerling
 * 
 */
public class Router implements org.petalslink.dsb.ukernel.binding.Router {

    public static List<Route> routes = new ArrayList<Route>();

    /**
     * 
     */
    public Router() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.binding.Router#getRoute()
     */
    public Route getRoute(String query) {
        // dummy implementation....
        for (Route route : routes) {
            if (route.getPath().equals(query)) {
                return route;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.binding.Router#addRoute(org.petalslink.
     * dsb.kernel.api.binding.Route)
     */
    public void addRoute(Route route) {
        if (route == null) {
            return;
        }
        routes.add(route);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.binding.Router#route(org.petalslink.dsb
     * .api.MessageExchange)
     */
    public Route route(MessageExchange message) {
        return null;
    }

}
