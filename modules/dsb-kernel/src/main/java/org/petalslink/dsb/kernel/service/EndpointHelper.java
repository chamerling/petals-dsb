/**
 * 
 */
package org.petalslink.dsb.kernel.service;

/**
 * @author chamerling
 * 
 */
public class EndpointHelper {

    private static EndpointHelper instance;

    public static EndpointHelper getInstance() {
        if (instance == null) {
            instance = new EndpointHelper();
        }
        return instance;
    }

    private EndpointHelper() {
    }

    public String getHost(String str) {
        String result = null;
        String without = getWithoutPrefix(str);
        if (without != null) {
            // look at the first '/'
            if (without.indexOf('/') > 0) {
                result = without.substring(0, without.indexOf('/'));
            }
        }
        return result;
    }

    public String getEndpoint(String str) {
        String result = null;
        String without = getWithoutPrefix(str);
        if (without.indexOf('/') > 0) {
            result = without.substring(without.indexOf('/') + 1);
        } else {
            result = without;
        }
        return result;
    }

    public String getWithoutPrefix(String string) {
        String result = null;
        if (string != null
                && string.startsWith(org.petalslink.dsb.kernel.service.Constants.PREFIX + "://")) {
            result = string.substring((org.petalslink.dsb.kernel.service.Constants.PREFIX + "://")
                    .length());
        }
        return result;
    }
}
