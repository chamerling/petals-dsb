/**
 * 
 */
package org.petalslink.dsb.integration.wsapi.mocks.ws;

/**
 * @author chamerling
 *
 */
public class PingMockServiceImpl implements PingMockService {

    public String ping(String input) {
        return input;
    }
}
