/**
 * 
 */
package org.petalslink.dsb.kernel.util;

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
}
