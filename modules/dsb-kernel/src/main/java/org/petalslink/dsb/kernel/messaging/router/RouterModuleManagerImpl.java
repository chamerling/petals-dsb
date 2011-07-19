/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;

/**
 * @author chamerling
 * 
 */
public class RouterModuleManagerImpl implements RouterModuleManager {

    private Map<String, SenderModule> senders;

    private Map<String, ReceiverModule> receivers;

    /**
     * 
     */
    public RouterModuleManagerImpl() {
        // to keep backward compatibility, modules are reverse alphabetically
        // ordered ie sender-02 have more priority than sender-01
        this.senders = new TreeMap<String, SenderModule>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        this.receivers = new TreeMap<String, ReceiverModule>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.SenderModule)
     */
    public void add(SenderModule module) {
        if (module != null && module.getName() != null) {
            this.senders.put(module.getName(), module);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.ReceiverModule)
     */
    public void add(ReceiverModule module) {
        if (module != null && module.getName() != null) {
            this.receivers.put(module.getName(), module);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#setState
     * (java.lang.String, boolean)
     */
    public void setState(String name, boolean onoff) {
        throw new NotImplementedException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getState
     * (java.lang.String)
     */
    public boolean getState(String name) {
        // TODO
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getSenders
     * ()
     */
    public List<SenderModule> getSenders() {
        List<SenderModule> result = new ArrayList<SenderModule>();
        for (SenderModule senderModule : senders.values()) {
            result.add(senderModule);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getReceivers
     * ()
     */
    public List<ReceiverModule> getReceivers() {
        List<ReceiverModule> result = new ArrayList<ReceiverModule>();
        for (ReceiverModule module : receivers.values()) {
            result.add(module);
        }
        return result;
    }

}
