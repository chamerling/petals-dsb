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
package org.petalslink.dsb.kernel.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.mortbay.jetty.webapp.WebAppContext;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class WebAppClassLoader extends org.mortbay.jetty.webapp.WebAppClassLoader {

    private final ClassLoader petalsClassLoader;

    private final Logger logger;

    /**
     * 
     */
    public WebAppClassLoader(ClassLoader petalsClassLoader, WebAppContext context, Logger logger)
            throws IOException {
        super(context);
        this.petalsClassLoader = petalsClassLoader;
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized URL getResource(String name) {
        URL result = null;
        result = this.petalsClassLoader.getResource(name);

        if (result == null) {
            result = super.getResource(name);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream result = null;
        result = this.petalsClassLoader.getResourceAsStream(name);

        if (result == null) {
            result = super.getResourceAsStream(name);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Class loadClass(String name) throws ClassNotFoundException {
        // try to load from petals class loader
        if (this.logger.isLoggable(BasicLevel.DEBUG)) {
            this.logger.log(BasicLevel.DEBUG, "Loading class '" + name + "'");
        }
        Class result = null;
        try {
            result = this.petalsClassLoader.loadClass(name);
        } catch (Exception e) {
            if (this.logger.isLoggable(BasicLevel.DEBUG)) {
                this.logger.log(BasicLevel.DEBUG, "Class '" + name
                        + "' not loaded from petals class loader");
            }
        }
        if (result == null) {
            result = super.loadClass(name);
            if (result != null) {
                if (this.logger.isLoggable(BasicLevel.DEBUG)) {
                    this.logger.log(BasicLevel.DEBUG, "Class '" + name
                            + "' loaded from jetty classloader");
                }
            }
        } else {
            if (this.logger.isLoggable(BasicLevel.DEBUG)) {
                this.logger.log(BasicLevel.DEBUG, "Class '" + name
                        + "' loaded from petals class loader");
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class result = null;
        if (this.logger.isLoggable(BasicLevel.DEBUG)) {
            this.logger.log(BasicLevel.DEBUG, "Loading Class '" + name + "' with resolve = '"
                    + resolve + "'");
        }
        try {
            result = super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
        }
        return result;
    }
}
