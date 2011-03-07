
package org.ow2.petals.esb.kernel.impl.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.registry.Registry;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.api.transport.listener.ListenersManager;
import org.ow2.petals.esb.kernel.impl.config.Configuration;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;
import org.ow2.petals.esb.kernel.impl.registry.RegistryImpl;
import org.ow2.petals.esb.kernel.impl.transport.TransportersManagerImpl;
import org.ow2.petals.esb.kernel.impl.transport.listener.ListenersManagerImpl;
import org.ow2.petals.transporter.api.transport.Transporter;
import org.ow2.petals.transporter.impl.soap.SOAPTransportContext;
import org.ow2.petals.transporter.impl.soap.SOAPTransporterImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller = "composite")
public class NodeImpl extends EndpointImpl implements Node {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(NodeImpl.class.getName());

    private List<Component> components = new ArrayList<Component>();

    private Registry registry;

    private TransportersManager transporter;

    private String host = "localhost";

    private String port;

    private static final String PORT = "9000";

    private Map<QName, Endpoint> listenedEndpoints = new HashMap<QName, Endpoint>();

    private ListenersManagerImpl listenersManager = new ListenersManagerImpl(
            ListenersManagerImpl.DEFAULT_THREAD_COUNTER, listenedEndpoints);

    public TransportersManager getTransportersManager() {
        return this.transporter;
    }

    public List<Component> getComponents() {
        return this.components;
    }

    public Registry getRegistry() {
        return this.registry;
    }

    public <C extends Component> C createComponent(QName name, String fractalInterfaceName,
            Class<C> componentClassName) throws ESBException {
        C component = null;
        try {
            org.objectweb.fractal.api.Component componentComponent = FractalHelper
                    .getFractalHelper().createNewComponent(componentClassName.getName(), null);
            FractalHelper.getFractalHelper().startComponent(componentComponent);

            if (name != null) {
                FractalHelper.getFractalHelper().changeName(componentComponent, name.toString());
            }

            component = (C) componentComponent.getFcInterface(fractalInterfaceName);

            // init
            component.initFractalComponent(componentComponent);
            component.setNode(this);

            // add component in list
            FractalHelper.getFractalHelper().addComponent(componentComponent, this.getComponent(),
                    null);
            this.components.add(component);

            // add component in registry
            this.getNode().getRegistry().addEndpoint(component);

            // add endpoint in listener
            // this.getListenedEndpoints().put(component.getQName(), component);

        } catch (NoSuchInterfaceException e) {
            throw new ESBException(e);
        } catch (FractalException e) {
            throw new ESBException(e);
        }
        log.fine("component " + name + " created and started");
        return component;
    }

    @Override
    public Node getNode() {
        return this;
    }

    public Registry createRegistry(String name) throws ESBException {
        Registry registry = null;
        try {
            org.objectweb.fractal.api.Component registryComponent = FractalHelper
                    .getFractalHelper().createNewComponent(RegistryImpl.class.getName(), null);
            FractalHelper.getFractalHelper().startComponent(registryComponent);

            if (name != null) {
                FractalHelper.getFractalHelper().changeName(registryComponent, name);
            } else {
                throw new ESBException("name cannot be null");
            }

            registry = (Registry) registryComponent.getFcInterface("service");

            // init
            registry.initFractalComponent(registryComponent);
            registry.setQName(new QName(this.getQName().getNamespaceURI(), name));
            registry.setNode(this);

            // add component in list
            FractalHelper.getFractalHelper().addComponent(registryComponent, this.getComponent(),
                    null);
            this.registry = registry;

            // add endpoint in registry
            this.registry.addEndpoint(registry);

            // add endpoint in listener
            // this.getListenedEndpoints().put(this.registry.getQName(),
            // this.registry);

        } catch (NoSuchInterfaceException e) {
            throw new ESBException(e);
        } catch (FractalException e) {
            throw new ESBException(e);
        }
        log.fine("registry " + name + " created and started");
        return registry;
    }

    public TransportersManager createTransportersManager(String name) throws ESBException {
        TransportersManager transporter = null;
        try {
            org.objectweb.fractal.api.Component transporterComponent = FractalHelper
                    .getFractalHelper().createNewComponent(TransportersManagerImpl.class.getName(),
                            null);
            FractalHelper.getFractalHelper().startComponent(transporterComponent);

            if (name != null) {
                FractalHelper.getFractalHelper().changeName(transporterComponent, name);
            } else {
                throw new ESBException("name cannot be null");
            }

            transporter = (TransportersManager) transporterComponent.getFcInterface("service");

            // init
            transporter.initFractalComponent(transporterComponent);
            transporter.setQName(new QName(this.getQName().getNamespaceURI(), name));
            transporter.setNode(this);

            // create SOAPTransporter
            Transporter soapTransporter = transporter.createTransporter("SOAPTransporter",
                    SOAPTransporterImpl.class);
            SOAPTransportContext context = new SOAPTransportContext();
            context.setNodeName(this.getQName());
            context.setHttpAddress("http://" + this.getHost() + ":" + this.getPort() + "/"
                    + "transporter");
            soapTransporter.setContext(context);

            // add component in list
            FractalHelper.getFractalHelper().addComponent(transporterComponent,
                    this.getComponent(), null);
            this.transporter = transporter;

            // add endpoint in registry
            this.registry.addEndpoint(transporter);

            // add endpoint in listener
            // this.getListenedEndpoints().put(this.transporter.getQName(),
            // this.transporter);

        } catch (NoSuchInterfaceException e) {
            throw new ESBException(e);
        } catch (FractalException e) {
            throw new ESBException(e);
        }
        log.fine("transporterManager " + name + " created and started");
        return transporter;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String ip) {
        this.host = ip;
    }

    public String getPort() {
        if (port == null) {
            String nodePort = Configuration.getData().get("node.port");
            System.out.println("PORT = " + nodePort);
            if (nodePort != null) {
                this.setPort(nodePort);
            } else {
                this.setPort(PORT);
            }
        }

        return port;
	}

    public void setPort(String port) {
        this.port = port;
    }

    public ListenersManager getListenersManager() {
        return this.listenersManager;
    }

    public Map<QName, Endpoint> getListenedEndpoints() {
        return this.listenedEndpoints;
    }

    public void setListenedEndpoints(Map<QName, Endpoint> listenedEndpoints) {
        this.listenedEndpoints = listenedEndpoints;
    }
}
