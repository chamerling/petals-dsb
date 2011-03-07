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

package org.ow2.petals.binding.soapproxy.listener.incoming.jetty;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A jetty logger implementation based on the root logger of the component.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JettyLogger implements org.mortbay.log.Logger {

    /**
     * The component logger
     */
    protected Logger logger;

    /**
     * The logger name
     */
    protected String name;

    /**
     * Creates a new instance of {@link JettyLogger}
     * 
     * @param name
     * @param logger
     */
    public JettyLogger(final String name, final java.util.logging.Logger logger) {
        this.name = name;
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#debug(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void debug(final String arg0, final Object arg1, final Object arg2) {
        this.logger.log(Level.FINE, arg0, new Object[] { arg1, arg2 });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    public void debug(final String arg0, final Throwable arg1) {
        this.logger.log(Level.FINE, arg0, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#getLogger(java.lang.String)
     */
    public org.mortbay.log.Logger getLogger(final String arg0) {
        if (arg0 == null) {
            return null;
        }

        return new JettyLogger(this.name + "." + arg0, this.logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#info(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void info(final String arg0, final Object arg1, final Object arg2) {
        this.logger.log(Level.INFO, arg0, new Object[] { arg1, arg2 });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#setDebugEnabled(boolean)
     */
    public void setDebugEnabled(final boolean arg0) {
        this.logger.setLevel(Level.FINE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#warn(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void warn(final String arg0, final Object arg1, final Object arg2) {
        this.logger.log(Level.WARNING, arg0, new Object[] { arg1, arg2 });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.log.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    public void warn(final String arg0, final Throwable arg1) {
        this.logger.log(Level.WARNING, arg0, arg1);
    }

    /**
     * Initialize the JEtty logger. We provide your own implementation based on
     * the standard logger.
     * 
     */
    public static void init() {
        final String old = System.getProperty("org.mortbay.log.class");
        try {
            System.setProperty("org.mortbay.log.class", JettyLogger.class.getName());
            // For the class to be loaded by invoking a public static method
            final Class cl = Thread.currentThread().getContextClassLoader().loadClass(
                    "org.mortbay.log.Log");
            cl.getMethod("isDebugEnabled", new Class[0]).invoke(null, null);
        } catch (final Exception e) {
        } finally {
            if (old != null) {
                System.setProperty("org.mortbay.log.class", old);
            } else {
                System.getProperties().remove("org.mortbay.log.class");
            }
        }
    }
}
