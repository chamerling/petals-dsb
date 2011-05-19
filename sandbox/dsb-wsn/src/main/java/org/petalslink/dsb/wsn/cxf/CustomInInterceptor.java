package org.petalslink.dsb.wsn.cxf;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;

public class CustomInInterceptor extends LoggingInInterceptor {

    private static final Logger LOG = LogUtils.getL7dLogger(LoggingInInterceptor.class);

    public CustomInInterceptor() {
        super();
        // TODO Auto-generated constructor stub
    }

    public CustomInInterceptor(int lim) {
        super(lim);
        // TODO Auto-generated constructor stub
    }

    public CustomInInterceptor(PrintWriter w) {
        super(w);
        // TODO Auto-generated constructor stub
    }

    public CustomInInterceptor(String phase) {
        super(phase);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        this.logging2(message);
    }

    private void logging2(Message message) throws Fault {
        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        final LoggingMessage buffer = new LoggingMessage(
                "Inbound Message\n----------------------------", id);

        String encoding = (String) message.get(Message.ENCODING);

        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);

        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        String uri = (String) message.get(Message.REQUEST_URI);
        if (uri != null) {
            buffer.getAddress().append(uri);
        }

        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
            CachedOutputStream bos = new CachedOutputStream();
            try {
                IOUtils.copy(is, bos);

                bos.flush();
                is.close();
                
                message.setContent(InputStream.class, bos.getInputStream());
                if (bos.getTempFile() != null) {
                    // large thing on disk...
                    buffer.getMessage().append("\nMessage (saved to tmp file):\n");
                    buffer.getMessage().append(
                            "Filename: " + bos.getTempFile().getAbsolutePath() + "\n");
                }
                if (bos.size() > getLimit()) {
                    buffer.getMessage().append("(message truncated to " + getLimit() + " bytes)\n");
                }
                bos.writeCacheTo(buffer.getPayload(), getLimit());
                String payload = IOUtils.toString(bos.getInputStream());
                System.out.println("IN : " + payload);
                bos.writeCacheTo(new StringBuilder(payload));

                bos.close();
            } catch (IOException e) {
                throw new Fault(e);
            }
        }

        if (getPrintWriter() != null) {
            getPrintWriter().println(transform(buffer.toString()));
        } else if (LOG.isLoggable(Level.INFO)) {
            LOG.info(transform(buffer.toString()));
        }
    }

}
