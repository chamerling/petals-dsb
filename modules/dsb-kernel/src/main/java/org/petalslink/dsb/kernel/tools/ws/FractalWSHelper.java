/**
 * 
 */
package org.petalslink.dsb.kernel.tools.ws;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.util.Fractal;
import org.ow2.petals.tools.ws.WebServiceHelper;

/**
 * @author chamerling - PetalsLink
 * 
 */
public class FractalWSHelper {

    public static final Set<WebServiceInformationBean> getAllBeans(
            final ContentController parentContentController) {
        Set<WebServiceInformationBean> result = new HashSet<WebServiceInformationBean>();
        Set<Component> components = getAllComponents(parentContentController);
        for (Component component : components) {
            try {
                String name = Fractal.getNameController(component).getFcName();
                Object[] itfs = component.getFcInterfaces();
                for (Object object : itfs) {
                    Class<?>[] cs = object.getClass().getInterfaces();
                    for (Class<?> class1 : cs) {
                        boolean isWs = WebServiceHelper.hasWebServiceAnnotation(class1);
                        if (isWs) {
                            WebServiceInformationBean bean = new WebServiceInformationBean();
                            bean.clazz = class1;
                            bean.componentName = name;
                            bean.implem = object;
                            result.add(bean);
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
    private static Set<Component> getAllComponents(final ContentController parentContentController) {
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

            // The component is a component, add it to the result list
            result.add(component);
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
            if (parents.length > 1) {
               // ??? many parent ie shared component... what to do?
            }
            if (parents.length == 1) {
                // goes up
                parent = getRootComponent(parents[0]);
            }
        } catch (NoSuchInterfaceException e) {
            // we are at the root level, stop going up
            return parent;
        }
        return parent;
    }

}
