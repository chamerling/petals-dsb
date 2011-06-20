/**
 * 
 */
package org.petalslink.dsb.annotations.utils;

import java.lang.annotation.Annotation;

/**
 * @author chamerling
 * 
 */
public class AnnotationUtils {

    public static <A extends Annotation> A getClassAnnotation(Class<?> c, Class<A> aClass) {
        if (c == null) {
            return null;
        }
        A p = c.getAnnotation(aClass);
        if (p != null) {
            return p;
        }

        p = getClassAnnotation(c.getSuperclass(), aClass);
        if (p != null) {
            return p;
        }

        // finally try the first one on the interface
        for (Class<?> i : c.getInterfaces()) {
            p = getClassAnnotation(i, aClass);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

}
