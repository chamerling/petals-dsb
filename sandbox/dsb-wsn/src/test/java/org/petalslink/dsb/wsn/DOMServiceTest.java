package org.petalslink.dsb.wsn;

import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.wsn.api.NotificationProducerDOM;

public class DOMServiceTest extends TestCase {

    public void testDOMService() throws InterruptedException {
        JaxWsServerFactoryBean serviceFactory = new JaxWsServerFactoryBean();
        serviceFactory.setAddress("http://localhost:9988/cxf/DOMService");
        serviceFactory.setServiceBean(new NotificationProducerDOM() {

            public void subcribe(StreamSource document) {
                System.out.println("GOT A MESSAGE : ");
                //System.out.println(WsstarCommonUtils.prettyPrint(document));
            }
        });
        serviceFactory.setServiceClass(NotificationProducerDOM.class);
        Server notificationProducerServiceServer = serviceFactory.create();
        
        Thread.sleep(900000L);
    }

}
