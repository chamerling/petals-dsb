package org.petalslink.dsb.component.logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.transform.TransformerException;

import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

/**
 * 
 * @author chamerling
 * 
 */
public class JBIListener extends AbstractJBIListener {

    @Override
    public boolean onJBIMessage(Exchange exchange) {
        StringBuffer sb = new StringBuffer(
                "####################### LOGGER ############################");
        sb.append("\n");
        try {
            Document in = exchange.getInMessageContentAsDocument(true);
            String payload = XMLHelper.createStringFromDOMDocument(in);
            sb.append("IN MESSAGE :");
            sb.append("\n");
            sb.append(payload);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        sb.append("\n");
        sb.append("OPERATION : ");
        sb.append(exchange.getOperation());
        sb.append("\n");
        sb.append("###########################################################");
        System.out.println(sb.toString());
        return true;
    }

}
