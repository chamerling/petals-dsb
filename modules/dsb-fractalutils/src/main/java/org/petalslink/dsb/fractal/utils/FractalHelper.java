/**
 * 
 */
package org.petalslink.dsb.fractal.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.util.Fractal;

/**
 * @author chamerling
 * 
 */
public class FractalHelper {

    /**
     * Get the top component of all the architecture.
     * 
     * @param component
     * @return
     */
    public static Component getRootComponent(Component component) {
        Component parent = null;
        try {
            Component parents[] = null;
            SuperController superController = Fractal.getSuperController(component);
            parents = superController.getFcSuperComponents();
            if (parents == null || parents.length == 0) {
                return component;
            }
            if (parents.length > 1) {
                // ??? many parent ie shared component... what to do?
            }
            if (parents.length == 1) {
                // goes up
                parent = getRootComponent(parents[0]);
            }
        } catch (NoSuchInterfaceException e) {
            parent = component;
        }
        return parent;
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
                if (o != null && o.getClass().isAnnotationPresent(annotation)) {
                    components.add(component);
                }
            } catch (NoSuchInterfaceException e) {
            }
        }
        return components;
    }

}
