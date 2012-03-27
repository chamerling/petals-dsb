
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.axis2.transport.TransportSender;

public class TransportSenderConfig extends AbstractNamedWithImplClassConfig {

    public TransportSenderConfig(String name, Class<? extends TransportSender> implClass) {
        super(name, implClass);
        assert implClass != null;
    }

    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<transportSender name='%s' class='%s'>\n", getName(),
                getImplClass().getName()));
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            writer.write(String.format("<parameter name='%s'>%s</parameter>\n", entry.getKey(),
                    entry.getValue()));
        }
        writer.write("</transportSender>");
    }



}
