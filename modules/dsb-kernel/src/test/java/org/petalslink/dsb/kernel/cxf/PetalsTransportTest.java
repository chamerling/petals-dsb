package org.petalslink.dsb.kernel.cxf;

import junit.framework.TestCase;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.io.client.ClientFactoryRegistry;
import org.petalslink.dsb.kernel.io.server.DSBServiceServer;
import org.petalslink.dsb.kernel.io.server.DSBServiceServerFactory;
import org.petalslink.dsb.kernel.io.server.ServerFactoryRegistry;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.ClientFactory;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.api.HelloService;

public class PetalsTransportTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSendReceive() throws Exception {
        DSBServiceServer server = createServer();
        ServerFactoryRegistry.setFactory(getServerFactory(server));
        ClientFactoryRegistry.setFactory(getClientFactory(server));
        
        final String in = "bar";
        final String pre = "foo";

        Bus bus = BusFactory.getDefaultBus();
        DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
        DSBTransportFactory petalsTransportFactory = new DSBTransportFactory();
        petalsTransportFactory.setBus(bus);
        petalsTransportFactory.registerWithBindingManager();

        // start service
        System.out.println("Starting CXF server...");
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setAddress("dsb://localhost/serviceA");
        sf.setServiceClass(HelloService.class);
        sf.setServiceBean(new HelloService() {
            public String sayHello(String input) throws DSBWebServiceException {
                System.out.println("SAY HELLO INVOKED!");
                return pre+input;
            }
        });
        sf.create();

        // invoke
        System.out.println("Client");
        // client
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress("dsb://localhost/serviceA");
        factory.setServiceClass(HelloService.class);
        HelloService hello = (HelloService) factory.create();
        String out = null;
        try {
            out = hello.sayHello(in);
        } catch (DSBWebServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
        
        assertEquals(pre+in, out);
    }

    private DSBServiceServerFactory getServerFactory(final DSBServiceServer server) {
        // fake
        DSBServiceServerFactory factory = new DSBServiceServerFactory() {
            
            public DSBServiceServer getServiceServer() {
                return server;
            }
        };
        return factory;
    }

    private ClientFactory getClientFactory(final DSBServiceServer server) {
        // TODO Auto-generated method stub
        return new ClientFactory() {
            
            public void release(Client client) {
                // TODO Auto-generated method stub
                
            }
            
            public Client getClient(ServiceEndpoint service) {
                return createClient(server);
            }
        };
    }

    private DSBServiceServer createServer() {
        return new DSBServiceServer() {

            MessageListener listener;

            public void stop() {
                System.out.println("Stop server");
            }

            public void start() {
                System.out.println("Start server");
            }

            public void setListener(MessageListener listener) {
                this.listener = listener;
            }

            public MessageListener getListener() {
                return listener;
            }
        };

    }

    private Client createClient(final DSBServiceServer server) {
        
        return new Client() {

            /**
             * Receive what is sent...
             */
            public Message sendReceive(Message message) throws ClientException {
                System.out.println("Send message " + message);
                
                // pass the message to the server listener...
                return server.getListener().onMessage(message);
            }

            public void sendAsync(Message message, MessageListener listener) throws ClientException {
                System.out.println("Send Async");
            }

            public void fireAndForget(Message message) throws ClientException {
                System.out.println("F&F");
            }
            
            /* (non-Javadoc)
             * @see org.petalslink.dsb.service.client.Client#getName()
             */
            public String getName() {
                return "ClientTest";
            }
        };
    }

}
