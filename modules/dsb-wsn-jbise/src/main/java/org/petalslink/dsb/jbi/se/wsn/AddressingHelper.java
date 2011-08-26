/**
 * 
 */
package org.petalslink.dsb.jbi.se.wsn;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author chamerling
 * 
 */
public class AddressingHelper {

    public static final boolean isInternalService(URI address) {
        boolean result = false;

        if (address == null) {
            return result;
        }
        return address.toString().startsWith(Constants.DSB_INTERNAL_SERVICE_NS);
    }

    public static final boolean isExternalService(URI address) {
        boolean result = false;

        if (address == null) {
            return result;
        }

        return address.toString().startsWith(Constants.DSB_EXTERNAL_SERVICE_NS)
                || !address.toString().startsWith(Constants.DSB_INTERNAL_SERVICE_NS);
    }

    public static final URI addLocation(URI uri, String component, String container, String domain) {
        // add the location to the initial URL

        String tmp = uri.toString();
        String pre = null;
        String post = null;
        StringBuffer sb = new StringBuffer();

        if (tmp != null) {
            int idx = tmp.indexOf("::");
            if (idx > 0) {
                pre = tmp.substring(0, idx);
                post = tmp.substring(idx + 2);
            } else {
                pre = tmp;
            }
        }

        sb.append(pre);
        sb.append("?");

        if (component != null) {
            sb.append("component=");
            sb.append(component);
            sb.append("&");
        }
        if (container != null) {
            sb.append("container=");
            sb.append(container);
            sb.append("&");
        }
        if (component != null) {
            sb.append("domain=");
            sb.append(domain);
        }

        if (post != null) {
            sb.append("::");
            sb.append(post);
        }

        return URI.create(sb.toString());
    }

    public static final String getContainer(URI uri) {
        return getParameter(uri, "container");
    }

    public static final String getComponent(URI uri) {
        return getParameter(uri, "component");
    }

    public static final String getDomain(URI uri) {
        return getParameter(uri, "domain");
    }

    public static String getQuery(URI uri) {
        String result = null;
        if (uri != null) {
            String tmp = uri.toString();
            int idx = tmp.indexOf("?");
            int stop = tmp.indexOf("::");
            if (idx >= 0) {
                if (stop > idx) {
                    result = tmp.substring(idx + 1, stop);
                } else {
                    result = tmp.substring(idx + 1);
                }
            }
        }
        return result;
    }

    public static String getParameter(URI uri, String name) {
        String result = null;
        String query = getQuery(uri);
        if (query != null) {
            result = parseQueryString(query).get(name);
        }
        return result;
    }

    public static Map<String, String> parseQueryString(String s) {
        Map<String, String> ht = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = (String) st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                ht.put(pair.toLowerCase(), "");
            } else {
                ht.put(pair.substring(0, pos).toLowerCase(), pair.substring(pos + 1));
            }
        }
        return ht;
    }

    /**
     * @param uri
     * @return
     */
    public static String getServiceName(URI uri) {
        String result = null;
        if (uri == null) {
            return result;
        }

        String tmp = uri.toString();
        int idx = tmp.indexOf("::");
        if (idx >= 0) {
            String post = tmp.substring(idx + 2);
            int i = post.indexOf("@");
            if (i >= 0) {
                result = post.substring(0, i);
            }
        }
        return result;
    }

    public static String getEndpointName(URI uri) {
        String result = null;
        if (uri == null) {
            return result;
        }

        String tmp = uri.toString();
        int idx = tmp.indexOf("::");
        if (idx >= 0) {
            String post = tmp.substring(idx + 2);
            int i = post.indexOf("@");
            if (i >= 0) {
                result = post.substring(i + 1);
            }
        }
        return result;
    }

    /**
     * @param uri
     * @return
     */
    public static String getInitialAddress(URI uri) {
        String result = null;
        if (uri == null) {
            return result;
        }

        String tmp = uri.toString();
        int idx = tmp.indexOf("::");
        if (idx >= 0) {
            result = tmp.substring(idx + 2);
        }
        return result;
    }

}
