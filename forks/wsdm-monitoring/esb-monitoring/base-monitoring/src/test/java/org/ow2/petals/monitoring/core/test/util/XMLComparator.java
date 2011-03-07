/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2006 EBM Websourcing
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $Id: XMLUtil.java 98 2006-02-24 16:18:48Z alouis $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.core.test.util;

import java.security.MessageDigest;

import org.apache.xml.security.c14n.Canonicalizer;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.w3c.dom.Document;

/**
 * Contains utilities methods for XML operations.
 * 
 * @author alouis - EBMWebsourcing
 * @author ddesjardins - EBMWebsourcing
 * @author gblondelle - EBMWebsourcing
 * @since 1.0
 * 
 */
public final class XMLComparator {

	public static int compare(final Document xml1, final Document xml2)
			throws Exception {
		final String hash1 = XMLComparator.getXMLSHA1Sign(xml1, true);
		final String hash2 = XMLComparator.getXMLSHA1Sign(xml2, true);
		return hash1.compareTo(hash2);
	}

	public static String getXMLSHA1Sign(final Document res,
			final boolean ommitComments) throws Exception {
		// Create a canonicalizer
		org.apache.xml.security.Init.init();
		;
		Canonicalizer canonicalizer = null;
		if (ommitComments) {
			canonicalizer = Canonicalizer
					.getInstance(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS);
		} else {
			canonicalizer = Canonicalizer
					.getInstance(Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS);
		}

		// Prettify the given xml input stream to remove unnecessary spaces and
		// tabs
		// ByteArrayOutputStream byteArrayOutputStream = new
		// ByteArrayOutputStream();
		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();
		// factory.setNamespaceAware(true);
		// Document res = factory.newDocumentBuilder().parse(inputStream);
		final String msg = XMLPrettyPrinter.prettyPrint(res);

		// XMLPrettyPrinter.prettyPrint(xmlDocument).prettify(inputStream,
		// byteArrayOutputStream);
		final byte[] bytes = msg.getBytes();

		// Canonicalize the prettified xml stream
		final byte[] canonBytes = canonicalizer.canonicalize(bytes);

		// Generate the SHA1 hash
		final MessageDigest hash = MessageDigest.getInstance("SHA1");
		final String hashString = new String(hash.digest(canonBytes));

		return hashString;
	}
}
