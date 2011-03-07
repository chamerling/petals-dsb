/**
 * PETALS - PETALS Services Platform. Copyright (c) 2006 EBM Websourcing,
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
 * $Id: SoapComponent.java 154 25 sept. 06 alouis $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soapproxy.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * A Specific SOAP Service Unit ClassLoader, in order to find classes contained
 * in the modules engaged with the services
 * 
 * @author cgirodengo
 * 
 */
public class SoapSUClassLoader extends URLClassLoader {

    /**
     * List of modules class loaders
     */
    private final List<ClassLoader> modulesClassLoaders;

    /**
     * SoapSUClassLoader is extended from URLClassLoader. The constructor does
     * not override the super constructor, but takes in addition a list of
     * modules ClassLoader to check
     * 
     * @param urls
     *            <code>URL</code>s
     * @param modulesClassLoaders
     *            the Modules ClassLoaders
     * @param parent
     *            parent classloader <code>ClassLoader</code>
     */
    public SoapSUClassLoader(final URL[] urls, final List<ClassLoader> modulesClassLoaders,
            final ClassLoader parent) {
        super(urls, parent);
        this.modulesClassLoaders = modulesClassLoaders;
    }

    /**
     * Overrides the URLClassLoader's findClass method by searching in the
     * modules ClassLoaders of the SU if the class was not found
     * 
     * @param name
     *            the name of the class
     * @return the resulting class
     * @exception ClassNotFoundException
     *                if the class could not be found
     */
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            clazz = super.findClass(name);
        } catch (final ClassNotFoundException e) {

            // We search the class on the modules
            if (this.modulesClassLoaders != null) {
                for (final ClassLoader moduleClassLoader : this.modulesClassLoaders) {
                    if (moduleClassLoader != null) {
                        try {
                            clazz = moduleClassLoader.loadClass(name);
                        } catch (final Exception me) {
                            // Nothing to do, continue
                        }

                        if (clazz != null) {
                            return clazz;
                        }
                    }
                }
            }

            throw e;
        }

        return clazz;
    }
}
