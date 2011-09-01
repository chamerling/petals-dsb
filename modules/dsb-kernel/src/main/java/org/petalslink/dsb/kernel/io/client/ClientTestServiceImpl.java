/**
 * 
 */
package org.petalslink.dsb.kernel.io.client;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.registry.api.util.XMLUtil;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ClientTestService.class) })
public class ClientTestServiceImpl implements ClientTestService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {

    }

    public boolean invoke(int times) {
        if (times <= 0) {
            return false;
        }
        
        ServiceEndpoint service = new ServiceEndpoint();
        service.setEndpointName("HelloServicePort");
        service.setServiceName(new QName("http://api.ws.dsb.petalslink.org/", "HelloServiceService"));
        QName itf = new QName("http://api.ws.dsb.petalslink.org/", "HelloService");
        service.setInterfaces(new QName[] { itf });

        Client client = null;
        try {
            client = ClientFactoryRegistry.getFactory().getClient(service);
            for (int i = 0; i < times; i++) {
                final int j = i;
                Message message = new Message() {

                    public Document getPayload() {
                        return XMLUtil
                                .createDocumentFromString("<api:sayHello xmlns:api=\"http://api.ws.dsb.petalslink.org/\"><arg0>Call "
                                        + j + "</arg0></api:sayHello>");
                    }

                    public QName getOperation() {
                        return new QName("http://api.ws.dsb.petalslink.org/", "sayHello");
                    }

                    public Map<String, String> getProperties() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public Map<String, Document> getHeaders() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public QName getService() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public QName getInterface() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public String getEndpoint() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public String getProperty(String name) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public void setProperty(String name, String value) {
                        // TODO Auto-generated method stub

                    }
                };

                System.out.println("Sending message to client");
                Message response = client.sendReceive(message);
                try {
                    System.out.println("Got response on DSB service client for call " + i + " : "
                            + XMLUtil.createStringFromDOMDocument(response.getPayload()));
                } catch (TransformerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } finally {
            try {
                ClientFactoryRegistry.getFactory().release(client);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
