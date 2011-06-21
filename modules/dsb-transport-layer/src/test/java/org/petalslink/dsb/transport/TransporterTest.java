/**
 * 
 */
package org.petalslink.dsb.transport;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;
import org.petalslink.dsb.transport.api.ClientFactory;
import org.petalslink.dsb.transport.api.Context;
import org.petalslink.dsb.transport.api.Receiver;

/**
 * @author chamerling
 * 
 */
public class TransporterTest extends TestCase {

    public void testNullMessage() throws Exception {
        System.out.println("TODO");
        assertTrue(true);
    }

    public void testNullFactory() throws Exception {
        System.out.println("TODO");
        assertTrue(true);
    }

    public void testSend() {
        final AtomicInteger counter = new AtomicInteger(0);
        final Transporter t = new Transporter();
        t.setClientFactory(new ClientFactory() {

            public void releaseClient(Context context, Client client) {

            }

            public Client getClient(Context context) {
                return new Client() {

                    public void send(MessageExchange exchange, long sendTimeout)
                            throws ClientException {
                        // let's call the client directly...
                        MessageExchange response = new MessageExchange();
                        response.setId(exchange.getId());
                        System.out
                                .println("On the client, just call back the transporter with its listener");
                        // TODO : on a new thread...
                        counter.incrementAndGet();
                        t.onMessage(response);
                    }
                };
            }
        });
        t.setTransportListener(new Receiver() {
            public void onMessage(MessageExchange message) {
                System.out
                        .println("Got a response on client, called from the transporter implementation");
                System.out.println(message);
            }
        });

        t.start();

        MessageExchange exchange = new MessageExchange();
        Context transportContext = new Context();
        transportContext.containerName = "localhost";
        try {
            t.send(exchange, transportContext);
        } catch (TransportException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(1, counter.intValue());
    }

    /**
     * Test sendsync. Let's call it in a separate thread, wait more than the
     * transporttimeout and look if we have a response...
     * 
     * @throws Exception
     */
    public void testSendSync() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final Transporter t = new Transporter();
        t.setClientFactory(new ClientFactory() {

            public void releaseClient(Context context, Client client) {

            }

            public Client getClient(Context context) {
                return new Client() {

                    public void send(MessageExchange exchange, long sendTimeout)
                            throws ClientException {
                        // let's call the client directly...
                        final MessageExchange response = new MessageExchange();
                        response.setId(exchange.getId());
                        response.getProperties().addAll(exchange.getProperties());
                        System.out
                                .println("On the client, just call back the transporter with its listener");
                        // call in another thread to since it is based on
                        // wait/notify and will not work on the same thread...
                        Thread thread = new Thread(new Runnable() {
                            public void run() {
                                t.onMessage(response);
                            }
                        });
                        thread.start();
                        System.out.println("Thread launched for response...");
                    }
                };
            }
        });
        t.setTransportListener(new Receiver() {
            public void onMessage(MessageExchange message) {
                // not usefull, should not be called, so we check it...
                counter.incrementAndGet();
            }
        });
        t.start();

        MessageExchange exchange = new MessageExchange();
        Context context = new Context();
        context.timeout = 10000L;
        context.containerName = "localhost";
        MessageExchange result = null;
        long start = System.currentTimeMillis();
        try {
            result = t.sendSync(exchange, context);
        } catch (TransportException e) {
            fail(e.getMessage());
        }
        long delta = System.currentTimeMillis() - start;
        assertTrue("The listener should not have beenn called!", 0 == counter.intValue());
        assertNotNull("The response is null and should not...", result);
        assertNotSame(result, exchange);
        assertTrue(String.format("The time it took %s ms must be less than the context one %s ms!", delta, context.timeout),
                delta < context.timeout);
    }
}
