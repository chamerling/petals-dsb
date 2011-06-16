/**
 * 
 */
package org.petalslink.dsb.kernel.tools.rest;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.util.Fractal;
import org.petalslink.dsb.annotations.service.RESTService;

/**
 * FIXME : TO be extracted since the same code is also available in the DSB
 * kernel.
 * 
 * @author chamerling
 * 
 */
public class FractalHelper {

    public static final Set<RESTServiceInformationBean> getAllRESTServices(final Component component) {
        Component root = FractalHelper.getRootComponent(component);
        Set<RESTServiceInformationBean> result = new HashSet<RESTServiceInformationBean>();
        List<Component> components = null;
        try {
            components = getAllComponentsWithAnnotation(
                    Fractal.getContentController(root), RESTService.class);
        } catch (NoSuchInterfaceException e1) {
            return result;
        }
        
        for (Component c : components) {
            try {
                Object content = c.getFcInterface("/content");
                String name = Fractal.getNameController(c).getFcName();
                RESTServiceInformationBean bean = new RESTServiceInformationBean();
                bean.componentName = name;
                bean.implem = content;
                result.add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * Get the root component from the given component point of view.
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

    public static boolean hasRESTServiceAnnotation(Class<?> cls) {
        if (cls == null) {
            return false;
        }
        if (null != cls.getAnnotation(RESTService.class)) {
            return true;
        }
        for (Class<?> inf : cls.getInterfaces()) {
            if (null != inf.getAnnotation(RESTService.class)) {
                return true;
            }
        }
        return hasRESTServiceAnnotation(cls.getSuperclass());
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
