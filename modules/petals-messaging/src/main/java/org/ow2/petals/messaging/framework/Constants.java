/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.messaging.framework;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Constants {

	static final String PREFIX = "com.ebmws.messaging.framework";

	class HTTPConstants {
		public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

		public static final String HEADER_CONTENT_TYPE = "Content-Type";

		public static final String STATUS_CODE = "Status-Code";

		public static final String HTTP_METHOD_GET = "GET";

		public static final String HTTP_METHOD_POST = "POST";

		public static final String HTTP_METHOD_DELETE = "DELETE";

		public static final String HTTP_METHOD_PUT = "PUT";

		public static final String COMPRESSION_GZIP = "gzip";

		public static final String HEADER_SERVER = "Server";
	}

	class MimeTypes {
		public static final String TYPE_MULTIPART_RELATED = "multipart/related";

		public static final String TYPE_SOAP = "application/soap+xml";

		public static final String TYPE_TEXT_XML = "text/xml";

		public static final String TYPE_TEXT_HTML = "text/html";

		public static final String TYPE_APPLICATION_XML = "application/xml";

		public static final String TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

		public static final String TYPE_TEXT_PLAIN = "text/plain";

		/**
		 * This is a default type...
		 */
		public static final String TYPE_RAW = "RAW";

	}

}
