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
    void setState(String name, boolean onoff);

    /**
     * 
     * @param name
     * @return
     */
    boolean getState(String name);

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
