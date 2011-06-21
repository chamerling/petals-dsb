/**
 * 
 */
package org.petalslink.dsb.jbi;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.petals.kernel.api.service.Location;
import org.ow2.petals.kernel.api.service.ServiceEndpoint;
import org.ow2.petals.kernel.api.service.ServiceEndpoint.EndpointType;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class AdapterTest extends TestCase {

    private static final String DOMAIN = "domain";

    private static final String COMPONENT = "component";

    private static final String CONTAINER = "container";

    private static String ENDPOINTNAME = "endpointName";

    private static QName SERVICENAME = QName.valueOf("ServiceName");

    private static QName ITF1 = QName.valueOf("itf1");

    private static QName ITF2 = QName.valueOf("itf2");
    
    private static Location LOCATION = new Location(DOMAIN, CONTAINER, COMPONENT);

    public void testNoLocation() throws Exception {

    }

    public void testNoDescription() throws Exception {

    }

    public void testNull() throws Exception {
        assertNull(Adapter
                .createServiceEndpoint((org.ow2.petals.kernel.api.service.ServiceEndpoint) null));
    }

    public void testFullyFilled() throws Exception {
        org.ow2.petals.kernel.api.service.ServiceEndpoint endpoint = new ServiceEndpoint() {

            public EndpointType getType() {
                return EndpointType.CONSUMER;
            }

            public void setType(EndpointType type) {
            }

            public String getEndpointName() {
                return ENDPOINTNAME;
            }

            public QName getServiceName() {
                return SERVICENAME;
            }

            public List<QName> getInterfacesName() {
                List<QName> list = new ArrayList<QName>();
                list.add(ITF1);
                return list;
            }

            public Document getDescription() {
                return null;
            }

            public Location getLocation() {
                return LOCATION;
            }

        };
        org.petalslink.dsb.api.ServiceEndpoint result = Adapter.createServiceEndpoint(endpoint);
        assertNotNull(result);
        assertEquals(result.getEndpointName(), endpoint.getEndpointName());
        assertEquals(result.getServiceName(), endpoint.getServiceName());
        assertNotNull(result.getComponentLocation());
        assertNotNull(result.getContainerLocation());
        assertNotNull(result.getSubdomainLocation());
        assertEquals(COMPONENT, result.getComponentLocation());
        assertEquals(CONTAINER, result.getContainerLocation());
        assertEquals(DOMAIN, result.getSubdomainLocation());
        assertNotNull(result.getInterfaces());
        assertEquals(1, result.getInterfaces().length);
        assertEquals(ITF1, result.getInterfaces()[0]);
        assertNull(result.getDescription());
        assertEquals(EndpointType.CONSUMER.toString(), result.getType());
        
    }
}
