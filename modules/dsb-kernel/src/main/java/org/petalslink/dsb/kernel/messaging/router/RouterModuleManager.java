/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router;

import java.util.List;

/**
 * Manages router modules
 * 
 * @author chamerling
 * 
 */
public interface RouterModuleManager {

    /**
     * 
     * @param module
     */
    void add(SenderModule module);

    /**
     * 
     * @param module
     */
    void add(ReceiverModule module);

    /**
     * 
     * @param name
     * @param onoff
     */
    void setSenderState(String name, boolean onoff);

    /**
     * 
     * @param name
     * @param onoff
     */
    void setReceiverState(String name, boolean onoff);

    /**
     * 
     * @param name
     * @return
     */
    boolean getReceiverState(String name);

    /**
     * 
     * @param name
     * @return
     */
    boolean getSenderState(String name);

    /**
     * 
     * @return
     */
    List<SenderModule> getSenders();

    /**
     * 
     * @return
     */
    List<ReceiverModule> getReceivers();

}
