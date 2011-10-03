/**
 * 
 */
package org.petalslink.dsb.jbi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.ow2.petals.jbi.messaging.endpoint.JBIServiceEndpointImpl;
import org.ow2.petals.jbi.messaging.exchange.NormalizedMessageImpl;
import org.ow2.petals.kernel.api.service.Location;
import org.ow2.petals.util.XMLUtil;
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

        if (serviceEndpoint.getDescription() != null) {
            try {
                se.setDescription(XMLUtil.createStringFromDOMDocument(serviceEndpoint
                        .getDescription()));
            } catch (TransformerException e) {
            }
        }

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

            if (serviceEndpoint.getDescription() != null) {
                se.setDescription(XMLUtil.createDocumentFromString(serviceEndpoint.getDescription()));
            }
        }
        return se;
    }

    public static Message transform(final NormalizedMessage in) {
        org.petalslink.dsb.service.client.MessageImpl result = new org.petalslink.dsb.service.client.MessageImpl();
        try {
            result.setPayload(XMLHelper.createDocument(in.getContent(), true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        @SuppressWarnings("rawtypes")
        Set keys = in.getPropertyNames();
        for (Object key : keys) {
            result.setProperty(key.toString(), in.getProperty(key.toString()).toString());
        }
        return result;
    }

    public static NormalizedMessage transform(final Message message) {

        NormalizedMessageImpl result = new NormalizedMessageImpl();
        try {
            Document doc = message.getPayload();
            if (doc != null) {
                doc.normalizeDocument();
                result.setContent(new DOMSource(doc));
            }
        } catch (MessagingException e1) {
            e1.printStackTrace();
        }
        if (message.getProperties() != null) {
            Set<String> keys = message.getProperties().keySet();
            for (String key : keys) {
                result.setProperty(key, message.getProperties().get(key));
            }
        }

        if (message.getHeaders() != null) {
            // TODO
        }
        return result;

    }
}
