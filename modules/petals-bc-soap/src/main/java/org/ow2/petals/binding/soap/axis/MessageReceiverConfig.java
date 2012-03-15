
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;

import org.apache.axis2.engine.MessageReceiver;

public class MessageReceiverConfig extends AbstractConfig {

    private final String mep;

    private final Class<? extends MessageReceiver> implClass;

    public MessageReceiverConfig(String mep, Class<? extends MessageReceiver> implClass) {
        assert mep != null;
        assert implClass != null;
        this.mep = mep;
        this.implClass = implClass;
    }

    
    String getMep() {
        return mep;
    }
    
    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<messageReceiver mep='%s' class='%s'/>", mep, implClass.getName()));
    }

}
