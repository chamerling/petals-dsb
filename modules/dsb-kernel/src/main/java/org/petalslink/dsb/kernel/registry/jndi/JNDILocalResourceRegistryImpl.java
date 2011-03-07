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
package org.petalslink.dsb.kernel.registry.jndi;

import java.util.Set;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.communication.jndi.client.JNDIService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.registry.LocalResourceRegistry;


/**
 * A JNDI based implementation of the resource registry. This is because JNDI is
 * the only thing which is shared between the Kernel runtime and the
 * components... The components will put information in the JNDI context and if
 * same (and good) strucutre is followed we will be able to expose components
 * resources to all.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JNDILocalResourceRegistryImpl implements LocalResourceRegistry {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "jndi", signature = JNDIService.class)
    private JNDIService jndiService;

    private Context usersContext;

    private boolean created = false;

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
    public synchronized void create() {
        if (!this.created) {
            try {
                this.usersContext = this.jndiService.getUsersContext();
            } catch (NamingException e) {
                this.log.warning(e.getMessage());
            }
        }
        this.created = true;
    }

    /**
     * {@inheritDoc}
     */
    public void createComponent(String componentName) {
        this.create();

        try {
            this.usersContext.createSubcontext(componentName);
        } catch (Exception e) {
            this.log.warning("Exception while creating component context : " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExposedServiceURLs(String componentName) {
        Set<String> result = null;
        try {
            NamingEnumeration<Binding> bindings = this.usersContext.listBindings(componentName);
            while (bindings.hasMoreElements()) {
                Binding binding = bindings.nextElement();
                Object object = binding.getObject();

                if (object instanceof Context) {
                    // get the Set of URLs
                    Context context = (Context) object;
                    Object o = context.lookup("services");
                    if (o != null) {
                        if (o instanceof Set<?>) {
                            result = (Set<String>) o;
                        } else {
                            this.log.warning("URLs can not be retrieved");
                        }
                    } else {
                        this.log.warning("Can not find services in component context");
                    }

                } else {
                    this.log.warning("This is not a good context for binding " + binding.getName());
                }

            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        // get the component context
        return result;
    }

    /**
     * Such as 'components/petals-bc-soap/services/url' will return the Object
     * which is bound at url
     */
    public Object getResource(String path) {
        Object result = null;
        String tmp = path;
        if ((tmp != null) && (tmp.length() >= 1) && (tmp.indexOf('/') == 0)) {
            tmp = tmp.substring(1, tmp.length());
        }

        if (tmp.lastIndexOf('/') == tmp.length()) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }

        String[] paths = tmp.split("/");
        Context c = this.usersContext;

        for (int i = 0; i < paths.length; i++) {
            String key = paths[i];
            Object o = null;
            try {
                o = c.lookup(key);
            } catch (NamingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (o == null) {
                System.out.println("Returning null for current key = " + key);
                return null;
            }

            if ((i < paths.length) && (o != null) && (o instanceof Context)) {
                c = (Context) o;
            } else {
                result = o;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void putResource(String path, Object o) {
        String tmp = path;
        if ((tmp != null) && (tmp.length() >= 1) && (tmp.indexOf('/') == 0)) {
            tmp = tmp.substring(1, tmp.length());
        }

        if (tmp.lastIndexOf('/') == tmp.length()) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }

        String[] paths = tmp.split("/");
        // create the intermediate context if needed...
        Context c = this.usersContext;
        for (String key : paths) {
            System.out.println("Creating context " + key);
            try {
                c = c.createSubcontext(key);
            } catch (NamingException e) {
                try {
                    c = (Context) c.lookup(key);
                } catch (NamingException e1) {
                }
            }
        }
    }

}
