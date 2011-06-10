/**
 * 
 */
package org.petalslink.dsb.jaxws;

import javax.jws.WebService;

/**
 * @author chamerling
 *
 */
public class JAXWSHelper {
    
    public static final Class<?> getWebServiceClass(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        if (null != cls.getAnnotation(WebService.class)) {
            return cls;
        }
        for (Class<?> inf : cls.getInterfaces()) {
            if (null != inf.getAnnotation(WebService.class)) {
                return inf;
            }
        }
        return getWebServiceClass(cls.getSuperclass());
    }

    public static final String getWebServiceName(Class<?> wsClass) {
        String serviceName = null;
        WebService anno = wsClass.getAnnotation(WebService.class);
        if ((anno.serviceName() == null) || (anno.serviceName().trim().length() == 0)) {
            serviceName = wsClass.getSimpleName();
        } else {
            serviceName = anno.serviceName();
        }
        return serviceName;
    }

    public static boolean hasWebServiceAnnotation(Class<?> cls) {
        if (cls == null) {
            return false;
        }
        if (null != cls.getAnnotation(WebService.class)) {
            return true;
        }
        for (Class<?> inf : cls.getInterfaces()) {
            if (null != inf.getAnnotation(WebService.class)) {
                return true;
            }
        }
        return hasWebServiceAnnotation(cls.getSuperclass());
    }


}
