/**
 * 
 */
package org.petalslink.dsb.ukernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.api.lifecycle.LifeCycleException;
import org.petalslink.dsb.kernel.api.lifecycle.LifeCycleManager;
import org.petalslink.dsb.kernel.api.lifecycle.STATE;
import org.petalslink.dsb.kernel.api.plugin.PluginManager;
import org.petalslink.dsb.ukernel.utils.LifeCycleHelper;

/**
 * @author chamerling
 * 
 */
public class PluginManagerImpl implements PluginManager {

    private static Logger logger = Logger.getLogger(PluginManagerImpl.class.getName());

    private final Map<Class<?>, Object> components;

    boolean initCalled;

    private STATE state;

    /**
     * 
     */
    protected PluginManagerImpl() {
        this.components = new ConcurrentHashMap<Class<?>, Object>();
        this.initCalled = false;
        this.state = STATE.STOPPED;
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws LifeCycleException {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Initializing engine");
        }
        LifeCycleManager lifeCycleManager = this.getComponent(LifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.preInit();
        }

        // get all the plugins which are implementing the LifeCyle interface in
        // order to start them
        List<Object> pluginsToInit = this.getComponents();
        for (Object component : pluginsToInit) {
            try {
                initComponent(component);
            } catch (DSBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (null != lifeCycleManager) {
            lifeCycleManager.postInit();
        }

        this.state = STATE.INITIALIZED;
        this.initCalled = true;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void start() throws LifeCycleException {

        if (!this.initCalled) {
            // call init before...
            this.init();
        }

        LifeCycleManager lifeCycleManager = this.getComponent(LifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.preStart();
        }

        // get all the plugins which are implementing the LifeCyle interface in
        // order to start them
        List<Object> pluginsToStart = this.getComponents();
        for (Object component : pluginsToStart) {
            try {
                startComponent(component);
            } catch (DSBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (null != lifeCycleManager) {
            lifeCycleManager.postStart();
        }
        this.state = STATE.STARTED;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void stop() {

        LifeCycleManager lifeCycleManager = this.getComponent(LifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.preStop();
        }

        // get all the plugins which are implementing the lifecycle in order to
        // stop them automatically
        List<Object> pluginsToStop = this.getComponents();
        for (Object component : pluginsToStop) {
            try {
                stopComponent(component);
            } catch (DSBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (null != lifeCycleManager) {
            lifeCycleManager.postStop();
        }
        this.state = STATE.STOPPED;
    }

    private boolean isStarted() {
        return STATE.STARTED.equals(this.getState());
    }

    /**
     * @return
     */
    private STATE getState() {
        return this.state;
    }

    public <T> void addComponent(Class<T> type, T plugin) throws DSBException {
        if (this.isStarted()) {
            throw new DSBException("Can not add plugin if engine is started");
        }
        if ((type == null) || (plugin == null)) {
            throw new DSBException("Can not add null plugin");
        }
        this.components.put(type, plugin);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getComponent(Class<T> type) {
        Object o = this.components.get(type);
        if (o != null) {
            return type.cast(o);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getComponents(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Object o : this.components.values()) {
            if (o != null) {
                try {
                    T object = type.cast(o);
                    result.add(object);
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    /**
     * Get all the components
     * 
     * @return
     */
    List<Object> getComponents() {
        return new ArrayList<Object>(components.values());
    }

    /**
     * @param component
     * @throws DSBException
     */
    private void initComponent(Object component) throws DSBException {
        LifeCycleHelper.invokeMethods(component, Phase.INIT);
    }

    /**
     * @param component
     * @throws DSBException
     */
    private void startComponent(Object component) throws DSBException {
        LifeCycleHelper.invokeMethods(component, Phase.START);
    }

    /**
     * @param component
     * @throws DSBException
     */
    private void stopComponent(Object component) throws DSBException {
        LifeCycleHelper.invokeMethods(component, Phase.STOP);

    }

}
