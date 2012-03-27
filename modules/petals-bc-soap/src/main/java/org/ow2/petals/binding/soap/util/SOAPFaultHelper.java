/**
 * PETALS - PETALS Services Platform. Copyright (c) 2005 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * ---------------------------------------------------------------------------
 * ---------------------------------------------------------------------------
 */
package org.ow2.petals.binding.soap.util;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;

/**
 * The SOAP Fault Helper.
 * 
 * @author Frederic Gardes
 */
public final class SOAPFaultHelper {
	/**
	 * The message by default for the faults.
	 */
	private static final String FAULT_STRING = "A fault occurs during the petals treatment";

	/**
	 * Create a SOAP fault.
	 * 
	 * @param soapFactory
	 *            The factory used to create the fault and identify the version
	 * @return the soap fault
	 */
	public static SOAPFault createSOAPFault(final SOAPFactory soapFactory) {
		final SOAPFault soapFault = soapFactory.createSOAPFault();
		final SOAPFaultCode soapFaultCode = soapFactory.createSOAPFaultCode();
		soapFaultCode.setText(soapFactory.getSOAPVersion().getReceiverFaultCode());
		soapFault.setCode(soapFaultCode);
		final SOAPFaultReason soapFaultReason = soapFactory.createSOAPFaultReason();
		soapFaultReason.setText(FAULT_STRING);
		soapFault.setReason(soapFaultReason);
		return soapFault;
	}

	/**
	 * Create a SOAP fault from the provided body content.
	 * 
	 * @param soapFactory
	 *            The factory used to create the fault and identify the version
	 * @return the soap fault
	 */
	@SuppressWarnings("unchecked")
	public static SOAPFault createSOAPFault(final SOAPFactory soapFactory, final OMElement bodyContent) {
		final SOAPFault soapFault = soapFactory.createSOAPFault();
		// a soap fault needs at least a code and a reason
		boolean codeFound = false;
		final SOAPFaultCode soapFaultCode = soapFactory.createSOAPFaultCode();
		boolean reasonFound = false;
		final SOAPFaultReason soapFaultReason = soapFactory.createSOAPFaultReason();
	    boolean roleFound = false;
	    final SOAPFaultRole soapFaultRole = soapFactory.createSOAPFaultRole();
		
		// a soap fault may have a detail
		boolean detailFound = false;

		final Iterator<OMElement> elements = bodyContent.getChildren();
		while (elements.hasNext()) {
			final OMElement element = elements.next();
			if (org.apache.axis2.namespace.Constants.QNAME_FAULTCODE.equals(element.getQName())
					|| org.apache.axis2.namespace.Constants.QNAME_FAULTCODE_SOAP12.equals(element.getQName())) {
				soapFaultCode.setText(element.getTextAsQName());
				codeFound = true;
			} else if (org.apache.axis2.namespace.Constants.QNAME_FAULTSTRING.equals(element.getQName())
					|| org.apache.axis2.namespace.Constants.QNAME_FAULTREASON_SOAP12.equals(element.getQName())) {
				soapFaultReason.setText(element.getText());
				reasonFound = true;
			} else if (org.apache.axis2.namespace.Constants.QNAME_FAULTACTOR.equals(element.getQName())
                    || org.apache.axis2.namespace.Constants.QNAME_FAULTSTRING.equals(element.getQName())) {
                soapFaultRole.setText(element.getText());
                roleFound = true;
            } else if (org.apache.axis2.namespace.Constants.QNAME_FAULTDETAILS.equals(element.getQName())
					|| org.apache.axis2.namespace.Constants.QNAME_FAULTDETAIL_SOAP12.equals(element.getQName())) {
				final SOAPFaultDetail soapFaultDetail = soapFactory.createSOAPFaultDetail();
				final Iterator it = element.getChildElements();
				while (it.hasNext()) {
					soapFaultDetail.addDetailEntry((OMElement) it.next());
				}
				soapFault.setDetail(soapFaultDetail);
				detailFound = true;
			}
		}
		// set the default value, if not found into the elements
		if (!codeFound) {
			soapFaultCode.setText(soapFactory.getSOAPVersion().getReceiverFaultCode());
		}
		if (!reasonFound) {
			soapFaultReason.setText(FAULT_STRING);
		}
		soapFault.setCode(soapFaultCode);
		soapFault.setReason(soapFaultReason);
	    if (roleFound) {
	        soapFault.setRole(soapFaultRole);
	    }
		// if no element found (code or reason or detail), we aren' consuming a
		// Soap proxified service. We're consuming a classic JBI service. So
		// the content (Source) of the JBI fault into the exchange is the detail
		// of the Soap Fault (Flat Binding)
		if (!codeFound && !reasonFound && !detailFound) {
			// the content is a real payload, not the content of a previous Soap
			// Fault
			final SOAPFaultDetail soapFaultDetail = soapFactory.createSOAPFaultDetail();
			soapFaultDetail.addChild(bodyContent);
			soapFault.setDetail(soapFaultDetail);
		}
		return soapFault;
	}

	/**
	 * Not allow to instantiate.
	 */
	private SOAPFaultHelper() {
		throw new UnsupportedOperationException(); // prevents calls from
		// subclass
	}
}
