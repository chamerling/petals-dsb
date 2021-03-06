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
package org.ow2.petals.binding.restproxy;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface HTTPConstants {

	static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

    static final String HEADER_CONTENT_TYPE = "Content-Type";

    static final String STATUS_CODE = "Status-Code";

	static final String HTTP_METHOD_GET = "GET";
	static final String HTTP_METHOD_POST = "POST";
	static final String HTTP_METHOD_DELETE = "DELETE";
	static final String HTTP_METHOD_PUT = "PUT";

	static final String COMPRESSION_GZIP = "gzip";

    static final String HEADER_SERVER = "Server";

}
