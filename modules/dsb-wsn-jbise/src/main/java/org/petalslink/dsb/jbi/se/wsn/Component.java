/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.petalslink.dsb.jbi.se.wsn;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.jbi.JBIException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.api.Wsdl;
import org.ow2.petals.component.framework.util.ServiceEndpointKey;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.ow2.petals.component.framework.util.XMLUtil;
import org.petalslink.dsb.notification.commons.PropertiesConfigurationProducer;
import org.petalslink.dsb.notification.commons.api.ConfigurationProducer;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * The dsb-wsn-jbise Binding Component.
 * 
 * <br>
 * <b>NOTE : </b>This class has to be used only if the component developper
 * wants to customize the main component class. In general, using the
 * org.objectweb.petals.component.framework.bc.DefaultBindingComponent class is
 * enough. If so, change the value in the JBI descriptor file.
 * 
 * @author
 * 
 */
public class Component extends PetalsBindingComponent {

    public static final String FILE_CFG = "notification.cfg";

    public static final String TOPICS_NS_FILE = "topics.xml";

    private static final String ENDPOINT_NAME = "endpoint";

    private static final String INTERFACE_NAME = "interface";

    private static final String SERVICE_NAME = "service";

    private static final String SUPPORTED_TOPICS = "supported-topics";

    NotificationEngine engine;

    private Map<ServiceEndpointKey, Wsdl> WSNEP;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.PetalsBindingComponent#postDoInit()
     */
    @Override
    protected void postDoInit() throws JBIException {

        this.WSNEP = new ConcurrentHashMap<ServiceEndpointKey, Wsdl>();

        // get the configuration files...
        Properties props = new Properties();
        try {
            props.load(Component.class.getClassLoader().getResourceAsStream("notification.cfg"));
        } catch (IOException e) {
            throw new JBIException("Can not find the notification configuration file");
        }

        URL url = Component.class.getClassLoader().getResource("topics.xml");
        if (url == null) {
            throw new JBIException("Can not find the notification topics configuration file");
        }

        String endpointName = props.getProperty(ENDPOINT_NAME);
        QName interfaceName = QName.valueOf(props.getProperty(INTERFACE_NAME));
        QName serviceName = QName.valueOf(props.getProperty(SERVICE_NAME));

        String tmp = props.getProperty(SUPPORTED_TOPICS);
        List<String> supportedTopics = new ArrayList<String>();
        if (tmp != null) {
            tmp = tmp.trim();
            String[] topics = tmp.split(",");
            if (topics != null) {
                for (String string : topics) {
                    if (string.trim().length() > 0) {
                        supportedTopics.add(string.trim());
                    }
                }
            }
        }

        if (engine == null) {
            engine = new NotificationEngine(getLogger(), url, supportedTopics, serviceName,
                    interfaceName, endpointName, getJBIClient());
        }
        this.engine.init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.AbstractComponent
     */
    @Override
    protected void doStart() throws JBIException {
        activateWSNEndpoints();

        // create default subscribers...

        // look if we have some configuration about subscribers...
        URL subscribers = Component.class.getClassLoader().getResource("subscribers.cfg");

        Properties subscriberProps = null;
        if (subscribers != null) {
            subscriberProps = new Properties();
            try {
                subscriberProps.load(Component.class.getClassLoader().getResourceAsStream(
                        "subscribers.cfg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (subscriberProps != null) {
            ConfigurationProducer producers = new PropertiesConfigurationProducer(subscriberProps);
            List<Subscribe> toSubscribe = producers.getSubscribe();
            for (Subscribe subscribe : toSubscribe) {
                // let's subscribe...
                try {
                    final com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse subscribeResponse = getNotificationEngine()
                            .getNotificationManager().getNotificationProducerEngine()
                            .subscribe(subscribe);

                    if (getLogger().isLoggable(Level.INFO)) {
                        getLogger().info("Subscribe response");
                        if (subscribeResponse != null) {
                            Document doc = Wsnb4ServUtils.getWsnbWriter()
                                    .writeSubscribeResponseAsDOM(subscribeResponse);
                            getLogger().info(XMLUtil.createStringFromDOMDocument(doc));
                        } else {
                            getLogger().info("None...");
                        }
                    }
                } catch (Exception e) {
                    if (getLogger().isLoggable(Level.FINE)) {
                        getLogger().log(Level.INFO, "Problem while subscribing", e);
                    }
                }
            }
        }
    }

    /**
     * @throws JBIException
     * 
     */
    private void activateWSNEndpoints() throws JBIException {
        List<Endpoint> endpointList = null;
        QName serviceName = null;
        String endpointName = null;

        try {
            endpointList = UtilFactory.getWSDLUtil().getEndpointList(
                    this.engine.getProducerWSDL().getDescription());
            if (endpointList != null) {
                final Iterator<Endpoint> iterator = endpointList.iterator();
                if (iterator != null) {
                    while (iterator.hasNext()) {
                        final Endpoint endpoint = iterator.next();
                        if (endpoint != null) {
                            serviceName = endpoint.getService().getQName();
                            endpointName = endpoint.getName();
                            this.activateWSNEndpoint(serviceName, endpointName,
                                    this.engine.getProducerWSDL());
                        }
                    }
                }
            }

            endpointList = UtilFactory.getWSDLUtil().getEndpointList(
                    this.engine.getConsumerWSDL().getDescription());
            if (endpointList != null) {
                final Iterator<Endpoint> iterator = endpointList.iterator();
                if (iterator != null) {
                    while (iterator.hasNext()) {
                        final Endpoint endpoint = iterator.next();
                        if (endpoint != null) {
                            serviceName = endpoint.getService().getQName();
                            endpointName = endpoint.getName();
                            this.activateWSNEndpoint(serviceName, endpointName,
                                    this.engine.getConsumerWSDL());
                        }
                    }
                }
            }
        } catch (WSDLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param serviceName
     * @param endpointName
     */
    private void activateWSNEndpoint(final QName serviceName, final String endpointName, Wsdl wsdl)
            throws JBIException {
        // add it before since activate endpoint will call getDescription
        // locally
        this.WSNEP.put(new ServiceEndpointKey(serviceName, endpointName), wsdl);
        ServiceEndpoint se = null;
        try {
            se = this.context.activateEndpoint(serviceName, endpointName);
        } catch (Exception e) {
            this.WSNEP.remove(new ServiceEndpointKey(serviceName, endpointName));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.AbstractComponent
     */
    @Override
    protected void doStop() throws JBIException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.AbstractComponent
     */
    @Override
    protected void doShutdown() throws JBIException {
    }

    // override some deprecated stuff...
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.component.framework.AbstractComponent#getServiceDescription
     * (javax.jbi.servicedesc.ServiceEndpoint)
     */
    @Override
    public Document getServiceDescription(ServiceEndpoint endpoint) {
        if (endpoint == null) {
            return null;
        }

        if (isNotification(endpoint)) {
            return getWSNDescription(endpoint);
        }
        return super.getServiceDescription(endpoint);
    }

    /**
     * @param endpoint
     * @return
     */
    private Document getWSNDescription(ServiceEndpoint endpoint) {
        Wsdl desc = WSNEP.get(new ServiceEndpointKey(endpoint.getServiceName(), endpoint
                .getEndpointName()));
        if (desc != null) {
            return desc.getDocument();
        }
        return null;
    }

    public NotificationEngine getNotificationEngine() {
        return engine;
    }

    /**
     * @param endpoint
     * @return
     */
    private boolean isNotification(ServiceEndpoint endpoint) {
        return WSNEP.get(new ServiceEndpointKey(endpoint.getServiceName(), endpoint
                .getEndpointName())) != null;
    }
}
