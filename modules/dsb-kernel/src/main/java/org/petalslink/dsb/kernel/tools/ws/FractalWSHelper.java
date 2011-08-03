/**
 * 
 */
package org.petalslink.dsb.kernel.tools.ws;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.util.Fractal;
import org.ow2.petals.tools.ws.WebServiceHelper;
import org.petalslink.dsb.fractal.utils.FractalHelper;
import org.petalslink.dsb.kernel.api.tools.ws.WebServiceInformationBean;

/**
 * @author chamerling - PetalsLink
 * 
 */
public class FractalWSHelper {

    public static final Set<WebServiceInformationBean> getAllBeans(
            final ContentController parentContentController) {
        Set<WebServiceInformationBean> result = new HashSet<WebServiceInformationBean>();
        Set<Component> components = FractalHelper.getAllComponents(parentContentController);
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
}
