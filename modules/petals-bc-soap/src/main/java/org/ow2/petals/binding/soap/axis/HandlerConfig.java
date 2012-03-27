
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;

import org.apache.axis2.engine.Handler;

public class HandlerConfig extends AbstractNamedWithImplClassConfig {

    public HandlerConfig(String name, Class<? extends Handler> implClass) {
        super(name, implClass);
        assert implClass != null;
    }

    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<handler name='%s' class='%s'/>\n", getName(),
                getImplClass().getName()));
    }

}
