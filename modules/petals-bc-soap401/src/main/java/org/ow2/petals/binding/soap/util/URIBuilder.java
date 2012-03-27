/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An URI Builder for REST
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 1.1
 * 
 */
public class URIBuilder {

    private static URIBuilder INSTANCE;

    /**
     * 
     * @return
     */
    public static URIBuilder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new URIBuilder();
        }
        return INSTANCE;
    }

    /**
     * 
     */
    private URIBuilder() {
    }

    /**
     * 
     * @param pattern
     * @param parameters
     * @return
     * @throws URISyntaxException
     * @throws URIBuilderException
     */
    // TODO: This method should be review to use URLEncoder insteadof String.replace()
    public URI build(final String pattern, final Map<String, String> parameters)
            throws URISyntaxException {
        URI result = null;
        String tmp = pattern;

        final Iterator<Map.Entry<String, String>> iter = parameters.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, String> entry = iter.next();
            String param;
            try {
                param = entry.getValue();
                param = param.replace("&amp;", "&");
                param = URLEncoder.encode(param, "UTF-8");
                param = param.replace("%23", "#");
                param = param.replace("%26", "&");
                param = param.replace("%3F", "?");
                param = param.replace("%3D", "=");

                tmp = tmp.replace("{" + entry.getKey() + "}", param);
            } catch (final UnsupportedEncodingException e) {
            }
        }
        tmp = tmp.replace("&amp;", "&");
        result = new URI(tmp);
        return result;
    }

    /**
     * 
     * @param uriPattern
     * @return
     */
    public Set<String> getParams(String uriPattern) {
        final Set<String> result = new HashSet<String>();

        int start = 0;
        while (((start = uriPattern.indexOf("{")) > -1)
                && ((uriPattern.indexOf("}") >= uriPattern.indexOf("{")))) {
            final int stop = uriPattern.indexOf("}");
            final String placeholder = uriPattern.substring(start + 1, stop);
            uriPattern = uriPattern.substring(stop + 1, uriPattern.length());
            result.add(placeholder);
        }

        return result;
    }
}
