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
package org.ow2.petals.component.framework;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public abstract class JNDIComponentInformation implements ComponentInformation {

    /**
	 * 
	 */
    public JNDIComponentInformation() {
    }

    public Set<String> getConsumedServices() {
        Set<String> result = null;
        InitialContext context = this.getInitialContext();
        String componentName = this.getComponentName();
        Context servicesContext = null;
        Context componentContext = null;
        Context componentsContext = null;

        if (context != null) {
            try {
                // create the component context
                componentsContext = context.createSubcontext("components");
            } catch (NamingException e) {
                try {
                    componentsContext = (Context) context.lookup("components");
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                // create the component context
                componentContext = componentsContext.createSubcontext(componentName);
            } catch (NamingException e) {
                try {
                    componentContext = (Context) componentsContext.lookup(componentName);
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            if (componentContext != null) {
                // get or create the services context
                try {
                    servicesContext = componentContext.createSubcontext("services");
                } catch (NamingException e) {
                    try {
                        servicesContext = (Context) componentContext.lookup("services");
                    } catch (NamingException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            Set<String> url = null;
            try {
                Object o = servicesContext.lookup("consume");
                if (o instanceof Set<?>) {
                    url = (Set<String>) o;
                }
            } catch (NamingException e) {
            }

            if (url == null) {
                url = new HashSet<String>();
                try {
                    servicesContext.bind("consume", url);
                } catch (NamingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            result = url;
        } else {
            result = null;
        }
        return result;
    }

    /**
     * @return
     */
    public abstract InitialContext getInitialContext();

    /**
     * {@inheritDoc}
     */
    public Set<String> getExposedServices() {
        Set<String> result = null;
        InitialContext context = this.getInitialContext();
        String componentName = this.getComponentName();
        Context servicesContext = null;
        Context componentContext = null;
        Context componentsContext = null;

        if (context != null) {
            try {
                // create the component context
                componentsContext = context.createSubcontext("components");
            } catch (NamingException e) {
                try {
                    componentsContext = (Context) context.lookup("components");
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                // create the component context
                componentContext = componentsContext.createSubcontext(componentName);
            } catch (NamingException e) {
                try {
                    componentContext = (Context) componentsContext.lookup(componentName);
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            if (componentContext != null) {
                // get or create the services context
                try {
                    servicesContext = componentContext.createSubcontext("services");
                } catch (NamingException e) {
                    try {
                        servicesContext = (Context) componentContext.lookup("services");
                    } catch (NamingException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            Set<String> url = null;
            try {
                Object o = servicesContext.lookup("expose");
                if (o instanceof Set<?>) {
                    url = (Set<String>) o;
                }
            } catch (NamingException e) {
            }

            if (url == null) {
                url = new HashSet<String>();
                try {
                    servicesContext.bind("expose", url);
                } catch (NamingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            result = url;
        } else {
            result = null;
        }
        return result;
    }

    /**
     * @return
     */
    protected abstract String getComponentName();

    /**
     * {@inheritDoc}
     */
    public String getProperty(String name) {
        String result = null;
        InitialContext context = this.getInitialContext();
        String componentName = this.getComponentName();
        Context servicesContext = null;
        Context componentContext = null;
        Context componentsContext = null;

        if (context != null) {
            try {
                // create the component context
                componentsContext = context.createSubcontext("components");
            } catch (NamingException e) {
                try {
                    componentsContext = (Context) context.lookup("components");
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                // create the component context
                componentContext = componentsContext.createSubcontext(componentName);
            } catch (NamingException e) {
                try {
                    componentContext = (Context) componentsContext.lookup(componentName);
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            if (componentContext != null) {
                // get or create the services context
                try {
                    servicesContext = componentContext.createSubcontext("properties");
                } catch (NamingException e) {
                    try {
                        servicesContext = (Context) componentContext.lookup("properties");
                    } catch (NamingException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            String property = null;
            try {
                Object o = servicesContext.lookup(name);
                if (o instanceof String) {
                    property = (String) o;
                } else {
                    System.out.println("Not a valid property " + name);
                }
            } catch (NamingException e) {
            }

            result = property;
        } else {
            result = null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void addProperty(String name, String value) {
        InitialContext context = this.getInitialContext();
        String componentName = this.getComponentName();
        Context servicesContext = null;
        Context componentContext = null;
        Context componentsContext = null;

        if (context != null) {
            try {
                // create the component context
                componentsContext = context.createSubcontext("components");
            } catch (NamingException e) {
                try {
                    componentsContext = (Context) context.lookup("components");
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                // create the component context
                componentContext = componentsContext.createSubcontext(componentName);
            } catch (NamingException e) {
                try {
                    componentContext = (Context) componentsContext.lookup(componentName);
                } catch (NamingException e1) {
                    e1.printStackTrace();
                }
            }

            if (componentContext != null) {
                // get or create the properties context
                try {
                    servicesContext = componentContext.createSubcontext("properties");
                } catch (NamingException e) {
                    try {
                        servicesContext = (Context) componentContext.lookup("properties");
                    } catch (NamingException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            try {
                servicesContext.bind(name, value);
            } catch (NamingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("Can not get context");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getProperties() {
        /*
         * Map<String, String> result = new HashMap<String, String>();
         * InitialContext context = this.getInitialContext(); String
         * componentName = this.getComponentName(); Context servicesContext =
         * null; Context componentContext = null; Context componentsContext =
         * null;
         * 
         * if (context != null) { try { // create the component context
         * componentsContext = context.createSubcontext("components"); } catch
         * (NamingException e) { try { componentsContext = (Context)
         * context.lookup("components"); } catch (NamingException e1) {
         * e1.printStackTrace(); } }
         * 
         * try { // create the component context componentContext =
         * componentsContext.createSubcontext(componentName); } catch
         * (NamingException e) { try { componentContext = (Context)
         * componentsContext.lookup(componentName); } catch (NamingException e1)
         * { e1.printStackTrace(); } }
         * 
         * if (componentContext != null) { // get or create the services context
         * try { servicesContext =
         * componentContext.createSubcontext("properties"); } catch
         * (NamingException e) { try { servicesContext = (Context)
         * componentContext.lookup("properties"); } catch (NamingException e1) {
         * e1.printStackTrace(); } } }
         * 
         * Map<String, String> props = null; try { Object o =
         * servicesContext.lookup("map"); if (o instanceof Map<?, ?>) { props =
         * (Map<String, String>) o; } } catch (NamingException e) { }
         * 
         * if (props == null) { props = new HashMap<String, String>(); try {
         * servicesContext.bind("map", props); } catch (NamingException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); } }
         * 
         * result = props; } else { result = null; } return result;
         */
        return null;
    }

}
