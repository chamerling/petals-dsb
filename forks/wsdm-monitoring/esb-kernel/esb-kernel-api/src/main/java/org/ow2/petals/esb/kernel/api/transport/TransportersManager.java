package org.ow2.petals.esb.kernel.api.transport;

import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.Transporter;


/**
 * The <code>Transporter</code> is used to send a <code>MessageExchange</code>
 * from a Petals container to another one. <br>
 * The local <code>Router</code> calls
 * <code>send(exchange,distantContainer)</code> on this <code>Transporter</code>
 * to send an exchange. <br>
 * This <code>Transporter</code> sends the <code>MessageExchange</code> to the
 * distant <code>Transporter</code>. <br>
 * When the distant <code>Transporter</code> receives the
 * <code>MessageExchange</code>, it has to call <code>receive(exchange)</code>
 * on its local <code>Router</code>. <br>
 * The localization of the distant container is in charge of the
 * <code>Transporter</code>. <br>
 * <br>
 * <em>Note that the destination container can be the same than this one.</em>
 * 
 * @version $Rev: 477 $ $Date: 2006-05-29 17:18:07 +0200 (lun., 29 mai 2006) $
 * @since Petals 1.0
 * @author alouis, rnaudin - EBM Websourcing
 */
@Interface(name="service")
public interface TransportersManager extends Endpoint, Transporter {
    
	void setQName(QName name);
	
	<T extends Transporter>  T createTransporter(String name, Class<T> clazz) throws ESBException;
    
	Map<UUID, WakeUpKey> getStub2awake();
	
	void stop();
}
