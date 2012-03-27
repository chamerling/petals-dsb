
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.axis2.transport.TransportListener;

public class TransportReceiverConfig extends AbstractNamedWithImplClassConfig {

    public TransportReceiverConfig(String name, Class<? extends TransportListener> implClass) {
        super(name, implClass);
        assert implClass != null;
    }

    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<transportReceiver name='%s' class='%s'>\n", getName(),
                getImplClass().getName()));
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            writer.write(String.format("<parameter name='%s'>%s</parameter>\n", entry.getKey(),
                    entry.getValue()));
        }
        writer.write("</transportReceiver>");
    }


}
