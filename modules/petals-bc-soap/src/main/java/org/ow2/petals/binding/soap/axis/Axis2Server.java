
package org.ow2.petals.binding.soap.axis;

import java.io.File;
import java.util.logging.Logger;

import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.ListenerManager;
import org.ow2.petals.binding.soap.SoapConstants;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;

public class Axis2Server extends AbstractAxis2RepositoryBasedObject {

    private static final Logger log = Logger
            .getLogger(SoapConstants.Component.LOGGER_COMPONENT_NAME);

    private ListenerManager listenerManager;

    public Axis2Server(File baseDir, Axis2Config config) {
        super(baseDir, config);
        this.listenerManager = null;
    }

    public void start() {
        log.fine(String.format("Starting Axis2 instance using '%s' as base directory.",
                getBaseDir()));

        listenerManager = new ListenerManager();
        listenerManager.init(getConfigurationContext());
        listenerManager.start();
    }

    public void stop() {
        log.fine(String.format("Stopping Axis2 instance having '%s' as base directory.",
                getBaseDir()));
        assert listenerManager != null : "Cannot stop an Axis2 instance which is not started!";
        if (listenerManager.isStopped())
            return;
        try {
            listenerManager.stop();
        } catch (AxisFault e) {
            throw new UncheckedException(e);
        }
    }

    public void deployService(ServiceConfig serviceConfig) {
        assert isSetUp() : "Axis2 instance must first be set up before deploying a service.";
        assert listenerManager == null : "Hot deploying a service after Axis2 server is started is not supported right now.";
        getRepository().deployService(serviceConfig);
    }

}
