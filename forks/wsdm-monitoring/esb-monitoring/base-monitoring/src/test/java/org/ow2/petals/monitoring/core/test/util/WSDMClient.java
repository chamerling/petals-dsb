package org.ow2.petals.monitoring.core.test.util;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.external.protocol.soap.impl.behaviour.proxy.SoapProviderProxyBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.soap.handler.SOAPSender;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class WSDMClient {

	public static boolean addNewExchange(final URI fileRequest,
			final String address) throws BusinessException {
		return WSDMClient.invokeOperation(fileRequest, address, null, false);
	}

	public static boolean addNewReportList(final URI fileRequest,
			final String address) throws BusinessException {
		return WSDMClient.invokeOperation(fileRequest, address, null, false);
	}

	private static boolean invokeOperation(final URI fileRequest,
			final String address, final URI expectedFileResponse,
			final boolean testJustIfResponseExist) throws BusinessException {
		boolean res = false;
		try {
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			dbFactory.setNamespaceAware(true);
			DocumentBuilder builder;

			builder = dbFactory.newDocumentBuilder();

			System.out.println("request file: " + fileRequest.toString());
			final Document request = builder.parse(fileRequest.toString());

			System.out.println("\n\nrequest: "
					+ XMLPrettyPrinter.prettyPrint(request));

			final SoapProviderProxyBehaviourImpl behaviour = new SoapProviderProxyBehaviourImpl(
					null);

			final Document response = new SOAPSender().sendSoapRequest(request,
					address);

			if (expectedFileResponse != null) {

				System.out.println("actual response: "
						+ XMLPrettyPrinter.prettyPrint(response));

				if (testJustIfResponseExist) {
					res = true;
				} else {
					final Document expectedResponse = builder
							.parse(expectedFileResponse.toString());

					final int val = XMLComparator.compare(response,
							expectedResponse);
					System.out.println("VAL = " + val);
					if (val == 0) {
						res = true;
					}
				}
			} else {
				res = true;
			}
		} catch (final ParserConfigurationException e) {
			throw new BusinessException(e);
		} catch (final SAXException e) {
			throw new BusinessException(e);
		} catch (final IOException e) {
			throw new BusinessException(e);
		} catch (final TransportException e) {
			throw new BusinessException(e);
		} catch (final Exception e) {
			throw new BusinessException(e);
		}
		return res;
	}

	public static boolean subcribe(final URI fileRequest, final String address,
			final URI expectedFileResponse) throws BusinessException {
		return WSDMClient.invokeOperation(fileRequest, address,
				expectedFileResponse, true);
	}

}
