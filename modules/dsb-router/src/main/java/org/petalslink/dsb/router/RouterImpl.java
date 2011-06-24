/**
 * 
 */
package org.petalslink.dsb.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.router.Router;
import org.petalslink.dsb.kernel.api.router.RouterModule;

/**
 * @author chamerling
 * 
 */
public class RouterImpl implements Router {

    private Map<String, RouterModule> modules;

    /**
     * 
     */
    public RouterImpl() {
        this.modules = new ConcurrentHashMap<String, RouterModule>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.router.RouterManager#getModule(java.lang
     * .String)
     */
    public RouterModule getModule(String name) {
        return modules.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.router.RouterManager#addModule(org.petalslink
     * .dsb.kernel.api.router.RouterModule)
     */
    public void addModule(RouterModule module) {
        if (module != null) {
            modules.put(module.getName(), module);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.router.RouterManager#deleteModule(java.
     * lang.String)
     */
    public RouterModule deleteModule(String name) {
        if (name == null) {
            return null;
        }
        return modules.remove(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.api.router.RouterModule#route(org.petalslink
     * .dsb.api.MessageExchange)
     */
    public List<ServiceEndpoint> route(MessageExchange message) {
        // the router is itself a module, it just goes inside all modules to resolve things...
        List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        // TODO : order modules based on some phases or priority
        for (RouterModule module : modules.values()) {
            try {
                result.addAll(module.route(message));
            } catch (DSBException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.router.RouterModule#getName()
     */
    public String getName() {
        return "router";
    }

}
