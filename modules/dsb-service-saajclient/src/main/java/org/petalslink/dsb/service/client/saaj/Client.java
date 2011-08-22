/**
 * 
 */
package org.petalslink.dsb.service.client.saaj;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.petalslink.dsb.saaj.utils.SOAPMessageUtils;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageImpl;
import org.petalslink.dsb.service.client.MessageListener;

/**
 * @author chamerling
 * 
 */
public class Client implements org.petalslink.dsb.service.client.Client {


    /**
     * 
     */
    public Client() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#fireAndForget(org.petalslink
     * .dsb.service.client.Message)
     */
    public void fireAndForget(Message message) throws ClientException {
        throw new ClientException("Not implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#sendReceive(org.petalslink.dsb
     * .service.client.Message)
     */
    public Message sendReceive(Message message) throws ClientException {
        if (message == null) {
            throw new ClientException("Message can not be null...");
        }

        QName operation = message.getOperation();
        if (operation == null) {
            throw new ClientException("Operation can not be null...");
        }
        try {
            SOAPMessage request = SOAPMessageUtils.createSOAPMessageFromBodyContent(message
                    .getPayload());
            MimeHeaders hd = request.getMimeHeaders();
            hd.addHeader("SOAPAction", operation.getLocalPart());
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();
            SOAPMessage response = connection.call(request, message.getEndpoint());

            MessageImpl responseMessage = new MessageImpl();
            responseMessage.setPayload(SOAPMessageUtils.getBodyFromMessage(response));
        } catch (UnsupportedOperationException e) {
            throw new ClientException(e);
        } catch (SOAPException e) {
            throw new ClientException(e);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#sendAsync(org.petalslink.dsb
     * .service.client.Message,
     * org.petalslink.dsb.service.client.MessageListener)
     */
    public void sendAsync(Message message, MessageListener listener) throws ClientException {
        throw new ClientException("Not implemented");

    }

}
