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
package org.ow2.petals.tools.generator.commons;

import java.lang.reflect.Constructor;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CreatorFactory {

    private static CreatorFactory INSTANCE;

    public synchronized static final CreatorFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CreatorFactory();
        }

        return INSTANCE;
    }

    /**
     * 
     */
    private CreatorFactory() {
    }

    public org.ow2.petals.tools.generator.commons.Creator getCreator(String version, String baseName) {
        org.ow2.petals.tools.generator.commons.Creator creator = null;
        String finalVersion = version;
        if (finalVersion.indexOf(".") >= 0) {
            finalVersion = finalVersion.replaceAll("\\.", "");
        }
        finalVersion = finalVersion.trim();

        // try to get the creator for the given version
        String className = baseName + finalVersion;
        try {
            creator = this.load(className);
        } catch (CreatorException e) {
            // try the major version one
            if (finalVersion.length() > 1) {
                String defaultClassName = baseName + finalVersion.charAt(0);
                try {
                    creator = this.load(defaultClassName);
                } catch (CreatorException e1) {
                }
            }
        }
        return creator;
    }

    private Creator load(String className) throws CreatorException {
        Creator c = null;
        Class<?> managerClass = null;

        try {
            managerClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new CreatorException("Can not find creator class " + className, e);
        }

        final Class<? extends Creator> subclass = managerClass.asSubclass(Creator.class);
        final Constructor<? extends Creator> constructor;
        try {
            constructor = subclass.getConstructor(Creator.class);

            try {
                c = constructor.newInstance();
            } catch (Exception e) {
                throw new CreatorException("Couldn't create creator of type " + className, e);
            }

        } catch (NoSuchMethodException e) {
            // go with the default constructor
            try {
                c = subclass.newInstance();
            } catch (Exception e1) {
                throw new CreatorException("Couldn't create creator of type " + className, e1);
            }
        }
        return c;
    }

}
