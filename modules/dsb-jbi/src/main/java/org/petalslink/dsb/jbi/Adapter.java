/**
 * 
 */
package org.petalslink.dsb.jbi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;

import org.ow2.petals.jbi.messaging.endpoint.JBIServiceEndpointImpl;
import org.ow2.petals.jbi.messaging.exchange.NormalizedMessageImpl;
import org.ow2.petals.kernel.api.service.Location;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.xmlutils.XMLHelper;
import org.w3c.dom.Document;

/**
 * @author chamerling
 *
 */
public class Adapter {
    
    public static org.petalslink.dsb.api.ServiceEndpoint createServiceEndpoint(
            org.ow2.petals.kernel.api.service.ServiceEndpoint serviceEndpoint) {
        if (serviceEndpoint == null) {
            return null;
        }
        org.petalslink.dsb.api.ServiceEndpoint se = new org.petalslink.dsb.api.ServiceEndpoint();
        if (serviceEndpoint.getLocation() != null) {
            se.setComponentLocation(serviceEndpoint.getLocation().getComponentName());
            se.setSubdomainLocation(serviceEndpoint.getLocation().getSubdomainName());
            se.setContainerLocation(serviceEndpoint.getLocation().getContainerName());
        }
        
        if (serviceEndpoint.getType() != null) {
            se.setType(serviceEndpoint.getType().toString());
        }

        // TODO : change description to string
        // se.setDescription(description);

        se.setEndpointName(serviceEndpoint.getEndpointName());
        se.setServiceName(serviceEndpoint.getServiceName());
        List<QName> itfs = serviceEndpoint.getInterfacesName();
        if (itfs != null) {
            se.setInterfaces(itfs.toArray(new QName[itfs.size()]));
        }
        return se;
    }

    public static JBIServiceEndpointImpl createServiceEndpoint(
            org.petalslink.dsb.api.ServiceEndpoint serviceEndpoint) {
        JBIServiceEndpointImpl se = new JBIServiceEndpointImpl();
        if (serviceEndpoint != null) {
            Location location = new Location(serviceEndpoint.getSubdomainLocation(),
                    serviceEndpoint.getContainerLocation(), serviceEndpoint.getComponentLocation());
            se.setLocation(location);

            se.setStringDescription(serviceEndpoint.getDescription());

            se.setEndpointName(serviceEndpoint.getEndpointName());
            se.setServiceName(serviceEndpoint.getServiceName());
            QName[] itfs = serviceEndpoint.getInterfaces();
            if (itfs != null) {
                List<QName> list = new ArrayList<QName>();
                for (QName qName : itfs) {
                    list.add(qName);
                }
                se.setInterfacesName(list);
            }
        }
        return se;
    }
    
    public static Message transform(final NormalizedMessage in) {
        return new Message() {

            public Document getPayload() {
                try {
                    return XMLHelper.createDocument(in.getContent(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            public QName getOperation() {
                return null;
            }

            public Map<String, String> getProperties() {
                Map<String, String> properties = new HashMap<String, String>();
                Set keys = in.getPropertyNames();
                for (Object key : keys) {
                    properties.put(key.toString(), in.getProperty(key.toString()).toString());
                }
                return properties;
            }

            public Map<String, Document> getHeaders() {
                return new HashMap<String, Document>();
            }

            public QName getService() {
                return null;
            }

            public QName getInterface() {
                return null;
            }

            public String getEndpoint() {
                return null;
            }

        };
    }

    public static NormalizedMessage transform(final Message message) {

        NormalizedMessageImpl result = new NormalizedMessageImpl();
        try {
            result.setContent(XMLHelper.createStreamSource(message.getPayload()));
        } catch (MessagingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Set<String> keys = message.getProperties().keySet();
        for (String key : keys) {
            result.setProperty(key, message.getProperties().get(key));
        }
        return result;

    }
}
