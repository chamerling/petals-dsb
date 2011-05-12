/**
 * 
 */
package org.petalslink.dsb.kernel.cxf;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jbi.JBIException;

import org.apache.cxf.Bus;
import org.apache.cxf.configuration.Configurer;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractTransportFactory;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.ConduitInitiator;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

/**
 * The transport factory is the input point from the CXF point of view to create
 * a Petals transporter.
 * 
 * @author chamerling
 * 
 */
public class DSBTransportFactory extends AbstractTransportFactory implements ConduitInitiator,
        DestinationFactory {

    /**
     * The transport ID to be registered in the CXF bus so that it is retrieved
     * when a destination is a Petals one.
     */
    public static final String TRANSPORT_ID = "http://cxf.apache.org/transports/"
            + org.petalslink.dsb.kernel.service.Constants.PREFIX;

    public static final List<String> DEFAULT_NAMESPACES = Arrays.asList(TRANSPORT_ID,
            "http://cxf.apache.org/transports/"
                    + org.petalslink.dsb.kernel.service.Constants.PREFIX + "/configuration");

    private Bus bus;

    private final Map<String, DSBDestination> destinationMap = new HashMap<String, DSBDestination>();

    private Collection<String> activationNamespaces = DEFAULT_NAMESPACES;

    public DSBTransportFactory() {
        this.setTransportIds(Arrays.asList(new String[] { TRANSPORT_ID }));
    }

    @Resource(name = "cxf")
    public void setBus(Bus b) {
        bus = b;
    }

    public Bus getBus() {
        return bus;
    }

    public Set<String> getUriPrefixes() {
        return Collections.singleton(org.petalslink.dsb.kernel.service.Constants.PREFIX);
    }

    public void setActivationNamespaces(Collection<String> ans) {
        activationNamespaces = ans;
    }

    @PostConstruct
    public void registerWithBindingManager() {
        if (null == bus) {
            return;
        }
        ConduitInitiatorManager cim = bus.getExtension(ConduitInitiatorManager.class);
        if (null != cim && null != activationNamespaces) {
            for (String ns : activationNamespaces) {
                cim.registerConduitInitiator(ns, this);
            }
        }
        DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
        if (null != dfm && null != activationNamespaces) {
            for (String ns : activationNamespaces) {
                dfm.registerDestinationFactory(ns, this);
            }
        }
    }

    public Conduit getConduit(EndpointInfo targetInfo) throws IOException {
        return getConduit(targetInfo, null);
    }

    /**
     * Conduit is used to send message from client to service
     */
    public Conduit getConduit(EndpointInfo endpointInfo, EndpointReferenceType target)
            throws IOException {
        Conduit conduit = new DSBConduit(target);
        Configurer configurer = bus.getExtension(Configurer.class);
        if (null != configurer) {
            configurer.configureBean(conduit);
        }
        return conduit;
    }

    /**
     * Destination is used to receive message from client
     */
    public Destination getDestination(EndpointInfo ei) throws IOException {
        DSBDestination destination = new DSBDestination(ei);
        Configurer configurer = bus.getExtension(Configurer.class);
        if (null != configurer) {
            configurer.configureBean(destination);
        }
        try {
            putDestination(ei.getService().getName().toString()
                    + ei.getInterface().getName().toString(), destination);
        } catch (JBIException e) {
            throw new IOException(e.getMessage());
        }
        return destination;
    }

    public void putDestination(String epName, DSBDestination destination) throws JBIException {
        if (destinationMap.containsKey(epName)) {
            throw new JBIException("DSBDestination for Endpoint " + epName
                    + " has already been created");
        } else {
            destinationMap.put(epName, destination);
        }
    }

    public DSBDestination getDestination(String epName) {
        return destinationMap.get(epName);
    }

    public void removeDestination(String epName) {
        destinationMap.remove(epName);
    }

}
