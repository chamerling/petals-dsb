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

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JNDIUtil {

    private static Log logger = LogFactory.getLog(JNDIUtil.class);

    private JNDIUtil() {

    }

    /**
     * Get an object from the given context (under path) and cast it
     * automatically to the given type.
     * 
     * @param <T>
     * @param c
     * @param context
     * @param path
     * @return
     */
    public static <T> T getObject(Class<T> c, Context context, String path, String name) {
        T result = null;

        Context ct = getContext(context, path);
        if (ct == null) {
            logger.warn("Can not find a valid context under " + path);
            // throw
        }

        Object o = null;
        try {
            o = ct.lookup(name);
        } catch (NamingException e) {
            logger.warn(e.getMessage());
        }

        if (o == null) {
            logger.debug("Null object under property '" + name + "' path");
            result = null;
        } else {
            // try to cast...
            try {
                result = c.cast(o);
            } catch (ClassCastException e) {
                logger.warn("Can not cast the object of class " + o.getClass().getCanonicalName()
                        + " to " + c.getCanonicalName());
            }
        }

        return result;
    }

    public static final Context getContext(Context rootContext, String path) {
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
        boolean found = false;
        int i = 0;
        while (!found && (i < paths.length)) {
            Object o = null;
            String p = paths[i++];
            logger.debug("Looking to context for path = " + p);
            try {
                o = c.lookup(p);
            } catch (NamingException e) {
                logger.warn(e);
            }

            if (o == null) {
                logger.debug("Nothing found for path = " + p);
                found = true;
            } else if (o instanceof Context) {
                logger.debug("Found something which is a Context for path = " + p);
                c = (Context) o;
                if (i == paths.length) {
                    logger.debug("Last entry, this is the context we return for path = " + p);

                    result = c;
                    found = true;
                }
            } else {
                logger.debug("Found something which is not a Context for path = " + p);
                found = true;
            }
        }
        return result;
    }

}
