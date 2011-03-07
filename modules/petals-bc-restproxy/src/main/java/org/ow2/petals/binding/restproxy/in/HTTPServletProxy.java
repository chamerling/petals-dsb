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
package org.ow2.petals.binding.restproxy.in;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ow2.petals.binding.restproxy.RESTException;

/**
 * The initial request is sent to the destination and the response will be
 * written to the original response stream.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface HTTPServletProxy {

    /**
     * Get the request from the {@link HttpServletRequest}, call the service
     * hosted on destination and write service response to
     * {@link HttpServletResponse}
     * 
     * @param destination
     *            REST service to call
     * @param request
     *            HTTP request
     * @param response
     *            HTTP response
     */
    void proxify(String destination, HttpServletRequest request, HttpServletResponse response)
            throws RESTException;

    /**
     * 
     * @param serviceName
     * @param path
     * @param request
     * @param response
     */
    public void invoke(String serviceName, String path, HttpServletRequest request,
            HttpServletResponse response) throws RESTException;
}
