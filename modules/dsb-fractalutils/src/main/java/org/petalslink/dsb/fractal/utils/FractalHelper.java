/**
 * 
 */
package org.petalslink.dsb.fractal.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
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

    private static Logger logger = Logger.getLogger(FractalHelper.class.getName());

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

    /**
     * Get all the component which are annotated with the given annotation at
     * the Class level.
     * 
     * @param parentContentController
     * @param annotation
     * @return
     */
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

    public static final List<Component> getAllComponentsWithMethodAnnotation(
            final ContentController parentContentController, Class<? extends Annotation> annotation) {
        List<Component> components = new ArrayList<Component>();

        ContentController subContentController = null;

        for (Component component : parentContentController.getFcSubComponents()) {
            try {
                subContentController = Fractal.getContentController(component);
                if (subContentController.getFcSubComponents().length > 0) {
                    components.addAll(getAllComponentsWithMethodAnnotation(subContentController,
                            annotation));
                }
            } catch (NoSuchInterfaceException e1) {
            }

            Object o = getContent(component);
            if (o != null) {
                Method[] methods = o.getClass().getMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(annotation)) {
                        components.add(component);
                    }
                }
            }
        }
        return components;
    }

    /**
     * Return the content controller of the given component is any available and
     * if the component is not null...
     * 
     * @param component
     * @return
     */
    public static final ContentController getContentController(final Component component) {
        ContentController cc = null;
        if (component != null) {
            try {
                cc = Fractal.getContentController(component);
            } catch (NoSuchInterfaceException e) {
                logger.debug(e.getMessage());
            }
        }
        return cc;
    }

    /**
     * The given component is a composite
     * 
     * @param component
     * @return
     */
    public static boolean isComposite(Component component) {
        boolean result = false;
        if (component != null) {
            try {
                ContentController controller = Fractal.getContentController(component);
                result = (controller.getFcSubComponents().length > 1);
            } catch (NoSuchInterfaceException e) {
                logger.debug(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Recursively get all the components (not the composites) from the given
     * content controller.
     * 
     * @param parentContentController
     * @return
     */
    public static Set<Component> getAllComponents(final ContentController parentContentController) {
        // the component to be returned
        Set<Component> result = new HashSet<Component>();

        // the subcomponent content controller
        ContentController subContentController = null;

        // List content controller subcomponents
        for (Component component : parentContentController.getFcSubComponents()) {
            // if the component is a composite (ie the number of subcomponents >
            // 0), search for components recursively
            try {
                subContentController = Fractal.getContentController(component);
                if (subContentController != null
                        && subContentController.getFcSubComponents().length > 0) {
                    result.addAll(getAllComponents(subContentController));
                }
            } catch (NoSuchInterfaceException e1) {
                // do nothing
            }

            // The component is a component, add it to the result list?
            if (!isComposite(component)) {
                result.add(component);
            }
        }
        return result;
    }

    /**
     * Get the content of a given component if it exists and if the given
     * component is not null
     * 
     * @param component
     * @return
     */
    public static final Object getContent(Component component) {
        Object result = null;
        try {
            result = component.getFcInterface("/content");
        } catch (NoSuchInterfaceException e) {
        }
        return result;
    }

}
