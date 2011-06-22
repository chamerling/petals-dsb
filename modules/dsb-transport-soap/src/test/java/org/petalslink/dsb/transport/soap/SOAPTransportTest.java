/**
 * 
 */
package org.petalslink.dsb.transport.soap;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.transport.TransporterImpl;
import org.petalslink.dsb.transport.api.Context;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.Server;

/**
 * Let's test several nodes...
 * 
 * @author chamerling
 * 
 */
public class SOAPTransportTest extends TestCase {

    public void test2Nodes() throws Exception {
        final AtomicInteger called = new AtomicInteger(0);
        final String host = "localhost";
        long port = 6666;
        long port2 = 7777;
        TransporterImpl transporter = new TransporterImpl();
        transporter.setClientFactory(new CXFClientFactory());
        transporter.setTransportListener(new Receiver() {
            public void onMessage(MessageExchange message) {
                System.out.println("Receive a message on host 1");
            }
        });
        Server server = new CXFServerImpl(host, port);
        server.setReceiver(transporter);
        transporter.setServer(server);
        transporter.start();
        
        TransporterImpl transporter2 = new TransporterImpl();
        transporter2.setClientFactory(new CXFClientFactory());
        transporter2.setTransportListener(new Receiver() {
            public void onMessage(MessageExchange message) {
                System.out.println("Receive a message on host 2");
                called.incrementAndGet();
            }
        });
        Server server2 = new CXFServerImpl(host, port2);
        server2.setReceiver(transporter2);
        transporter2.setServer(server2);
        transporter2.start();
        
        // send a message from 1 to 2
        Context context = new Context();
        context.containerName = host;
        context.hostName = host;
        context.port = port2;
        transporter.send(new MessageExchange(), context);
        
        assertTrue("The server 2 may have been called...", called.intValue() == 1);
        
    }
}
