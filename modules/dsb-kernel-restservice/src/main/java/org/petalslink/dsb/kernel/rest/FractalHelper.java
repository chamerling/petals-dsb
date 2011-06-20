/**
 * 
 */
package org.petalslink.dsb.kernel.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.util.Fractal;
import org.petalslink.dsb.annotations.service.CoreService;
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
        Component root = org.petalslink.dsb.fractal.utils.FractalHelper.getRootComponent(component);
        Set<RESTServiceInformationBean> result = new HashSet<RESTServiceInformationBean>();
        List<Component> components = null;
        try {
            components = org.petalslink.dsb.fractal.utils.FractalHelper
                    .getAllComponentsWithAnnotation(Fractal.getContentController(root),
                            RESTService.class);
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

    public static boolean hasRESTServiceAnnotation(Class<?> cls) {
        if (cls == null) {
            return false;
        }
        if (null != cls.getAnnotation(CoreService.class)
                && cls.getAnnotation(CoreService.class).type().endsWith("rest")) {
            return true;
        }
        for (Class<?> inf : cls.getInterfaces()) {
            if (null != inf.getAnnotation(CoreService.class)
                    && cls.getAnnotation(CoreService.class).type().endsWith("rest")) {
                return true;
            }
        }
        return hasRESTServiceAnnotation(cls.getSuperclass());
    }

}
