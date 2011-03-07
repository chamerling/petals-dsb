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

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import org.ow2.petals.component.framework.util.UtilFactory;
import org.ow2.petals.component.framework.util.XMLUtil;
import org.w3c.dom.Document;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class HTTPUtils {

    private HTTPUtils() {
    }

    public static Source getContentAsSource(HttpServletRequest request) {
        Source result = null;
        try {
            if ((request != null) && (request.getInputStream() != null)) {
                Document doc = XMLUtil.loadDocument(request.getInputStream());
                if (doc != null) {
                    result = UtilFactory.getSourceUtil().createStreamSource(doc);
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static String getContentType(String str) {
        if (str == null) {
            return null;
        }
        String contentType = str;
        String type = null;
        if (contentType != null) {
            int index = contentType.indexOf(';');
            if (index > 0) {
                type = contentType.substring(0, index);
            } else {
                type = contentType;
            }
        }
        return type;
    }

    /**
     * @param target
     * @param request
     * @return
     */
    public static String decode(String target, HttpServletRequest request) {
        String result = target;

        if ((request != null) && (target != null)) {
            String encoding = request.getCharacterEncoding();
            if (encoding == null) {
                encoding = Constants.DEFAULT_ENCODING;
            }
            try {
                result = URLDecoder.decode(target, encoding);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return result;
    }

    public static final void writeXMLError(Exception e, OutputStream os) {
        StringBuffer sb = new StringBuffer("<Error>");
        if (e != null) {
            sb.append(e.getMessage());
        } else {
            sb.append("Got an error with no associated message");
        }
        sb.append("</Error>");

        try {
            os.write(sb.toString().getBytes());
        } catch (Exception e2) {
        }
    }
}
