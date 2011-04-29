/**
 * 
 */
package org.petalslink.gms;

import java.net.InetAddress;

/**
 * A peer in the domain
 * 
 * @author chamerling
 * 
 */
public class Peer {

    private String name;

    private InetAddress address;

    public Peer(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

}
