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

import javax.xml.namespace.QName;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Constants {

    static final String PORT = "port";

    static final int DEFAULT_PORT = 8787;

    static final String HOST = "hostname";

    static final String DEFAULT_HOST = "localhost";

    static final String PATH = "path";

    static final String DEFAULT_PATH = "petals/rest/service";

    public static final String PREFIX = "org.ow2.petals.rest.proxy";

    static final String NAMESPACE = "http://petals.ow2.org";

    static final String SERVICE = "RESTService";

    static final String INTERFACE = "RESTInterface";

    static final QName SERVICE_NAME = new QName(NAMESPACE, SERVICE);

    static final String ENDPOINT = "RESTEndpoint";

    static final QName INTERFACE_NAME = new QName(NAMESPACE, INTERFACE);

    static final String OPERATION = "operation";

    static final String RAW = "rawdata";

    // default
    static final String DEFAULT_ENCODING = "UTF-8";

    static final String DEFAULT_PROXY_PATH = "petals/rest/proxy";

    static final String PROXY_PATH = "proxy";

}
