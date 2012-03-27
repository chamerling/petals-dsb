/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.notification.Notify;
import org.petalslink.dsb.fractal.utils.FractalHelper;

/**
 * Scans the framework to discover notification aware services...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = NotificationConsumerScanner.class) })
public class NotificationConsumerScannerImpl implements NotificationConsumerScanner {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private org.objectweb.fractal.api.Component component;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.pubsub.service.internal.NotificationConsumerScanner
     * #scan()
     */
    public List<NotificationTargetBean> scan() {
        System.out.println("Scanning...");
        List<NotificationTargetBean> result = new ArrayList<NotificationTargetBean>();
        Component root = FractalHelper.getRootComponent(component);
        if (root != null) {
            List<Component> components = FractalHelper.getAllComponentsWithMethodAnnotation(
                    FractalHelper.getContentController(root), Notify.class);
            if (components != null) {
                for (Component component : components) {
                    String componentName = FractalHelper.getName(component);

                    // get the topic names
                    Object o = FractalHelper.getContent(component);
                    // get all the methods which are annotated with Notify
                    if (o != null) {
                        for (Method m : o.getClass().getMethods()) {
                            if (m.isAnnotationPresent(Notify.class)) {
                                log.debug(String.format(
                                        "Found notify annotation on method %s for component %s",
                                        m.getName(), componentName));
                                
                                // just accept one argument method which is a dom document...
                                Notify n = m.getAnnotation(Notify.class);
                                NotificationTargetBean target = new NotificationTargetBean();
                                target.m = m;
                                target.target = o;
                                target.topic = n.topics();
                                target.mode = n.mode();
                                target.id = componentName;
                                result.add(target);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
