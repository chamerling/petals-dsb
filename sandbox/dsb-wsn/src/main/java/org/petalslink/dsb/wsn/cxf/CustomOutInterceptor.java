/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.message.Message;

/**
 * @author chamerling
 * 
 */
public class CustomOutInterceptor extends LoggingOutInterceptor {

    private int limit = 100 * 1024;

    protected PrintWriter writer = new PrintWriter(System.out);

    @Override
    public void handleMessage(Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        if (os == null) {
            return;
        }
        final CacheAndWriteOutputStream cos = new CacheAndWriteOutputStream(os);
        message.setContent(OutputStream.class, cos);

        System.out.println("OUT MESSAGE AT INTERCEPTOR LEVEL : ");
        System.out.println();
        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        final LoggingMessage buffer = new LoggingMessage(
                "Outbound Message\n---------------------------", id);

        String encoding = (String) message.get(Message.ENCODING);

        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }

        String address = (String) message.get(Message.ENDPOINT_ADDRESS);
        if (address != null) {
            buffer.getAddress().append(address);
        }
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(headers);
        }

        if (cos.getTempFile() == null) {
            // buffer.append("Outbound Message:\n");
            if (cos.size() > limit) {
                buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
            }
        } else {
            buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
            buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
            if (cos.size() > limit) {
                buffer.getMessage().append("(message truncated to " + limit + " bytes)\n");
            }
        }
        try {
            cos.writeCacheTo(buffer.getPayload(), limit);
        } catch (Exception ex) {
            // ignore
        }

        writer.println("OUT MESSAGE : ");
        writer.println(transform(buffer.toString()));
        writer.flush();
        try {
            // empty out the cache
            cos.lockOutputStream();
            cos.resetOut(null, false);
        } catch (Exception ex) {
            // ignore
        }
        message.setContent(OutputStream.class, cos);
    }

}
