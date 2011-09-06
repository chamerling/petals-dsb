/**
 * 
 */
package org.petalslink.dsb.component.wsnpoller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.su.AbstractServiceUnitManager;
import org.ow2.petals.component.framework.su.ServiceUnitDataHandler;
import org.ow2.petals.component.framework.util.XMLUtil;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollerService;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingTransport;
import org.petalslink.dsb.service.poller.api.ServiceInformation;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class SUManager extends AbstractServiceUnitManager {

    private PollingTransport transport;

    private Map<String, Jbi> cache;

    private Map<String, List<PollingContext>> contexts;

    private Map<String, List<PollerService>> services;

    private Component pollerComponent;

    public SUManager(Component component) {
        super(component);
        this.pollerComponent = component;
        this.transport = component;
        this.cache = new HashMap<String, Jbi>();
        this.contexts = new HashMap<String, List<PollingContext>>();
        this.services = new HashMap<String, List<PollerService>>();
    }

    @Override
    protected void doDeploy(String serviceUnitName, String suRootPath, Jbi jbiDescriptor)
            throws PEtALSCDKException {
        this.cache.put(serviceUnitName, jbiDescriptor);
        List<Consumes> consumes = jbiDescriptor.getServices().getConsumes();
        List<PollingContext> contexts = new ArrayList<PollingContext>();
        for (Consumes consume : consumes) {
            contexts.add(createContext(consume, suRootPath));
        }
        this.contexts.put(serviceUnitName, contexts);

    }

    private PollingContext createContext(Consumes consume, String suRootPath) {
        PollingContext context = null;
        ServiceUnitDataHandler dh = this.getSUDataHandlerForConsumes(consume);
        if (dh != null) {
            ConfigurationExtensions extensions = dh.getConfigurationExtensions(consume);
            String cron = extensions.get("cron");
            ServiceInformation responseTo = new ServiceInformation();
            responseTo.endpoint = extensions.get("endpoint");
            responseTo.itf = extensions.get("interface") != null ? QName.valueOf(extensions
                    .get("interface")) : null;
            responseTo.operation = extensions.get("operation") != null ? QName.valueOf(extensions
                    .get("operation")) : null;
            responseTo.service = extensions.get("service") != null ? QName.valueOf(extensions
                    .get("service")) : null;
            responseTo.service = extensions.get("service") != null ? QName.valueOf(extensions
                    .get("service")) : null;
            ServiceInformation toPoll = new ServiceInformation();
            toPoll.endpoint = consume.getEndpointName();
            toPoll.itf = consume.getInterfaceName();
            toPoll.service = consume.getServiceName();
            toPoll.operation = consume.getOperation();
            context = new PollingContext();
            context.setCron(cron);
            context.setInputMessage(createInputMessage(new File(suRootPath, extensions
                    .get("inputFile"))));
            context.setJob(new ServiceInvokeToWSNJob());
            context.setResponseTo(responseTo);
            context.setToPoll(toPoll);
            context.setTransport(this.transport);
            
            context.getExtensions().put("topicName", extensions.get("topicName"));
            context.getExtensions().put("topicURI", extensions.get("topicURI"));
            context.getExtensions().put("topicPrefix", extensions.get("topicPrefix"));
        }
        return context;
    }

    private Document createInputMessage(File f) {
        Document result = null;
        if (f != null && f.isFile()) {
            try {
                result = XMLUtil.loadDocument(new FileInputStream(f));
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        } else {
            logger.warning("Can not load a document from an unknow file : " + f);
        }
        return result;
    }

    @Override
    protected void doInit(String serviceUnitName, String suRootPath) throws PEtALSCDKException {
        List<PollingContext> contexts = this.contexts.get(serviceUnitName);
        if (contexts == null || contexts.size() == 0) {
            return;
        }

        List<PollerService> services = new ArrayList<PollerService>();
        for (PollingContext pollingContext : contexts) {
            try {
                services.add(pollerComponent.getPollingManager().getPollerService(pollingContext));
            } catch (PollerException e) {
                throw new PEtALSCDKException("Problem while creating poller service", e);
            }
        }
        this.services.put(serviceUnitName, services);
    }

    @Override
    protected void doShutdown(String serviceUnitName) throws PEtALSCDKException {
    }

    @Override
    protected void doStart(String serviceUnitName) throws PEtALSCDKException {
        List<PollerService> services = this.services.get(serviceUnitName);
        for (PollerService service : services) {
            try {
                service.start();
            } catch (PollerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doStop(String serviceUnitName) throws PEtALSCDKException {
        List<PollerService> services = this.services.get(serviceUnitName);
        for (PollerService service : services) {
            try {
                service.stop();
            } catch (PollerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doUndeploy(String serviceUnitName) throws PEtALSCDKException {
        this.contexts.remove(serviceUnitName);
        this.services.remove(serviceUnitName);
    }

}
