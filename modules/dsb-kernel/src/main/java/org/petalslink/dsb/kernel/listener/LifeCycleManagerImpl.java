/**
 * 
 */
package org.petalslink.dsb.kernel.listener;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.kernel.util.FractalHelper;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = LifeCycleManager.class) })
public class LifeCycleManagerImpl implements LifeCycleManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @org.objectweb.fractal.fraclet.annotation.annotations.Service(name = "component")
    private Component component;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    public void onStart() {
        try {
            SuperController sc = Fractal.getSuperController(this.component);
            if (sc.getFcSuperComponents().length != 1) {
                this.log.warning("Can not find a super component to look for startup annotations in the architecture");
            } else {
                Component parentcontainer = sc.getFcSuperComponents()[0];
                ContentController cc = Fractal.getContentController(parentcontainer);
                List<Component> components = FractalHelper.getAllComponentsWithAnnotation(cc,
                        LifeCycleListener.class);

                // order
                Map<Integer, Set<Bean>> map = new TreeMap<Integer, Set<Bean>>(
                        new Comparator<Integer>() {
                            public int compare(Integer o1, Integer o2) {
                                int result = 0;
                                if (o1.intValue() > o2.intValue()) {
                                    result = -1;
                                } else if (o1.intValue() == o2.intValue()) {
                                    result = 0;
                                } else {
                                    result = 1;
                                }
                                return result;
                            }
                        });
                this.getBeansForPhase(components, map, Phase.START);
                this.invoke(map);

            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    private void invoke(Map<Integer, Set<Bean>> map) {

        for (Integer key : map.keySet()) {
            if (log.isInfoEnabled()) {
                this.log.info("Calling services for level : " + key);
            }
            Set<Bean> set = map.get(key);
            for (Bean bean : set) {
                try {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("Invoking " + bean.m.getName() + " on "
                                + bean.o.getClass().getName());
                    }
                    bean.m.invoke(bean.o, new Object[0]);
                } catch (Exception e) {
                    log.warning(e.getMessage());
                }
            }
        }
    }

    private void getBeansForPhase(List<Component> components, Map<Integer, Set<Bean>> map,
            Phase phase) throws NoSuchInterfaceException {
        for (Component c : components) {
            Object content = c.getFcInterface("/content");
            Method[] methods = content.getClass().getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(LifeCycleListener.class)
                        && m.getAnnotation(LifeCycleListener.class).phase() == phase) {
                    Bean bean = new Bean();
                    bean.m = m;
                    bean.o = content;
                    bean.priority = m.getAnnotation(LifeCycleListener.class).priority();
                    if (map.get(bean.priority) == null) {
                        map.put(new Integer(bean.priority), new HashSet<Bean>(/*
                                                                               * Sort
                                                                               * by
                                                                               * name
                                                                               * ?
                                                                               */));
                    }
                    map.get(bean.priority).add(bean);
                }
            }
        }
    }

    public void onStop() {
        try {
            SuperController sc = Fractal.getSuperController(this.component);
            if (sc.getFcSuperComponents().length != 1) {
                this.log.warning("Can not find a super component to look for shutdown annotations in the architecture");
            } else {
                Component parentcontainer = sc.getFcSuperComponents()[0];
                ContentController cc = Fractal.getContentController(parentcontainer);
                List<Component> components = FractalHelper.getAllComponentsWithAnnotation(cc,
                        LifeCycleListener.class);

                // TODO : Fix the order of the set with the Comparator
                Map<Integer, Set<Bean>> map = new TreeMap<Integer, Set<Bean>>(
                        new Comparator<Integer>() {
                            public int compare(Integer o1, Integer o2) {
                                int result = 0;
                                if (o1.intValue() < o2.intValue()) {
                                    result = -1;
                                } else if (o1.intValue() == o2.intValue()) {
                                    result = 0;
                                } else {
                                    result = 1;
                                }
                                return result;
                            }
                        });
                this.getBeansForPhase(components, map, Phase.STOP);
                this.invoke(map);

            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    class Bean {
        Method m;

        int priority;

        Object o;
    }

    public void preStart() {
        this.log.warning("Not implemented");
    }

    public void preStop() {
        this.log.warning("Not implemented");
    }

    public void preShutdown() {
        this.log.warning("Not implemented");
    }

    public void onShutdown() {
        this.log.warning("Not implemented");
    }
}
