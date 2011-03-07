package org.ow2.petals.monitoring.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import petals.ow2.org.exchange.PatternType;

public class Util {

	public static Exchange createMessageExchange(
			final ClientEndpoint sourceEndpoint,
			final ProviderEndpoint providerEndpoint, final QName interfaceName,
			final QName operation, final PatternType mep, final String msg)
			throws TransportException {
		Exchange me = null;

		try {
			me = sourceEndpoint.createExchange();
			me.setPattern(mep);
			me.setDestination(providerEndpoint.getQName());
			me.setInterface(interfaceName);
			me.setOperation(operation.toString());

			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			final Document doc = factory.newDocumentBuilder().parse(
					new ByteArrayInputStream(msg.getBytes()));
			me.getIn().getBody().setContent(doc);
		} catch (final SAXException e) {
			throw new TransportException(e);
		} catch (final IOException e) {
			throw new TransportException(e);
		} catch (final ParserConfigurationException e) {
			throw new TransportException(e);
		} catch (final ExchangeException e) {
			throw new TransportException(e);
		}

		return me;
	}
}
