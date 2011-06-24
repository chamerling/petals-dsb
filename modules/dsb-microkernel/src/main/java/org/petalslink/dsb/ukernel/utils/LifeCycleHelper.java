/**
 * 
 */
package org.petalslink.dsb.ukernel.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 * 
 */
public class LifeCycleHelper {

    /**
     * @param component
     */
    public static void invokeMethods(Object component, Phase phase) throws DSBException {
        List<Method> methods = getMethods(component, LifeCycleListener.class, phase);
        for (Method method : methods) {
            try {
                method.invoke(component, new Object[0]);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static List<Method> getMethods(Object component, Class<LifeCycleListener> annotation,
            Phase phase) {
        List<Method> result = new ArrayList<Method>();
        if (component != null) {
            Method[] methods = component.getClass().getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(annotation)
                        && phase.equals(m.getAnnotation(annotation).phase())) {
                    result.add(m);
                }
            }
        }
        return result;
    }
}
