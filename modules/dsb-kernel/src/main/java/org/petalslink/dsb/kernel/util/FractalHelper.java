/**
 * 
 */
package org.petalslink.dsb.kernel.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.util.Fractal;

/**
 * @author chamerling
 * 
 */
public class FractalHelper {

    public static <T> List<T> getAll(Class<T> clazz,
            final ContentController parentContentController, String serviceName) {
        List<T> result = new ArrayList<T>();

        ContentController subContentController = null;

        for (Component component : parentContentController.getFcSubComponents()) {
            try {
                subContentController = Fractal.getContentController(component);
                if (subContentController.getFcSubComponents().length > 0) {
                    result.addAll(getAll(clazz, subContentController, serviceName));
                }
            } catch (NoSuchInterfaceException e1) {
            }

            try {
                Object o = component.getFcInterface(serviceName);
                if (o != null) {
                    try {
                        result.add(clazz.cast(o));
                    } catch (ClassCastException e) {
                    }
                }
            } catch (NoSuchInterfaceException e) {
            }
        }
        return result;
    }

    public static final List<Component> getAllComponentsWithAnnotation(
            final ContentController parentContentController, Class<? extends Annotation> annotation) {
        List<Component> components = new ArrayList<Component>();

        ContentController subContentController = null;

        for (Component component : parentContentController.getFcSubComponents()) {
            try {
                subContentController = Fractal.getContentController(component);
                if (subContentController.getFcSubComponents().length > 0) {
                    components.addAll(getAllComponentsWithAnnotation(subContentController,
                            annotation));
                }
            } catch (NoSuchInterfaceException e1) {
            }

            try {
                Object o = component.getFcInterface("/content");
                if (o != null) {
                    Method[] methods = o.getClass().getMethods();
                    for (Method m : methods) {
                        if (m.isAnnotationPresent(annotation)) {
                            components.add(component);
                        }
                    }
                }
            } catch (NoSuchInterfaceException e) {
            }
        }
        return components;
    }

}
