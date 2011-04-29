/**
 * 
 */
package org.petalslink.gms;

/**
 * Message exchanged between peers
 * 
 * @author chamerling
 * 
 */
public class GMSMessage {

    public enum Type {
        PING, HELLO, LEAVE, JOIN, STATUS
    }

    /**
     * The source of the message
     */
    Peer source;

    public Peer getSource() {
        return source;
    }

    public void setSource(Peer source) {
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Message type
     */
    Type type;

}
