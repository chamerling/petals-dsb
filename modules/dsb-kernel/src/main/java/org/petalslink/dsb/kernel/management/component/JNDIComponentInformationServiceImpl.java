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
package org.petalslink.dsb.kernel.management.component;

import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.communication.jndi.client.JNDIService;
import org.ow2.petals.util.LoggingUtil;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ComponentInformationService.class) })
public class JNDIComponentInformationServiceImpl implements ComponentInformationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "jndi", signature = JNDIService.class)
    private JNDIService jndiService;

    private static final Set<String> EMPTYSET = new HashSet<String>(0);

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getConsumedServiceURLs(String componentName) {
        // have a look under /users/components/$componentName/services/consume
        Set<String> result = null;

        // have a look under /users/components/$componentName/services/expose
        Context c = this.getContext("/components/" + componentName + "/services/");
        if (c == null) {
            this.log.warning("Nothing found for services in component context " + componentName);
            return EMPTYSET;
        }

        // get the exposed services
        Object o = null;
        try {
            o = c.lookup("consume");
        } catch (NamingException e) {
            this.log.warning(e.getMessage());
        }

        if (o == null) {
            this.log.debug("Null object under consume path");
            result = EMPTYSET;
        } else if (o instanceof Set<?>) {
            result = (Set<String>) o;
        } else {
            this.log.debug("No Set<?> object under consume path");
            result = EMPTYSET;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExposedServiceURLs(String componentName) {
        Set<String> result = null;

        // have a look under /users/components/$componentName/services/expose
        Context c = this.getContext("/components/" + componentName + "/services/");
        if (c == null) {
            this.log.warning("Nothing found for services in component context " + componentName);
            return EMPTYSET;
        }

        // get the exposed services
        Object o = null;
        try {
            o = c.lookup("expose");
        } catch (NamingException e) {
            this.log.warning(e.getMessage());
        }

        if (o == null) {
            this.log.debug("Null object under expose path");
            result = EMPTYSET;
        } else if (o instanceof Set<?>) {
            result = (Set<String>) o;
        } else {
            this.log.debug("No Set<?> object under expose path");
            result = EMPTYSET;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getProperty(String componentName, String name) {
        String result = null;

        // have a look under /users/components/$componentName/services/expose
        Context c = this.getContext("/components/" + componentName + "/properties/");
        if (c == null) {
            this.log.warning("Nothing found for properties in component context " + componentName);
            return null;
        }

        // get the exposed services
        Object o = null;
        try {
            o = c.lookup(name);
        } catch (NamingException e) {
            this.log.warning(e.getMessage());
        }

        if (o == null) {
            this.log.debug("Null object under property '" + name + "' path");
            result = null;
        } else if (o instanceof String) {
            result = (String) o;
        } else {
            this.log.debug("No String object under property path");
            result = null;
        }
        return result;
    }

    private Context getContext(String path) {
        Context result = null;
        String tmp = path;
        if ((tmp != null) && (tmp.length() >= 1) && (tmp.indexOf('/') == 0)) {
            tmp = tmp.substring(1, tmp.length());
        }

        if (tmp.lastIndexOf('/') == tmp.length()) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }

        String[] paths = tmp.split("/");
        // get the user context first, and start the search from here...
        Context c = null;
        try {
            c = this.jndiService.getUsersContext();
        } catch (NamingException e) {
            this.log.warning("Can not find the users context");
            return null;
        }
        boolean found = false;
        int i = 0;
        while (!found && (i < paths.length)) {
            Object o = null;
            String p = paths[i++];
            this.log.debug("Looking to context for path = " + p);
            try {
                o = c.lookup(p);
            } catch (NamingException e) {
                this.log.warning(e);
            }

            if (o == null) {
                this.log.debug("Nothing found for path = " + p);
                found = true;
            } else if (o instanceof Context) {
                this.log.debug("Found something which is a Context for path = " + p);
                c = (Context) o;
                if (i == paths.length) {
                    this.log.debug("Last entry, this is the context we return for path = " + p);

                    result = c;
                    found = true;
                }
            } else {
                this.log.debug("Found something which is not a Context for path = " + p);
                found = true;
            }
        }
        return result;
    }
}
