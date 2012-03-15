
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.axis2.engine.Handler;

public class PhaseConfig extends AbstractNamedWithImplClassConfig {

    private final Map<String, HandlerConfig> handlers;

    public PhaseConfig(String name) {
        this(name, null);
    }
    
    public PhaseConfig(String name, Class<? extends Handler> implClass) {
        super(name, implClass);
        this.handlers = new LinkedHashMap<String, HandlerConfig>();
    }

    
    @Override
    public void dump(Writer writer) throws IOException {
        if (getImplClass() == null) {
            writer.write(String.format("<phase name='%s'>\n", getName()));
        } else {
            writer.write(String.format("<phase name='%s' class='%s'>\n", getName(), getImplClass().getName()));
        }
        for (HandlerConfig handler : handlers.values()) {
            handler.dump(writer);
        }
        writer.write("</phase>");
    }

    public void addHandler(HandlerConfig handlerConfig) {
        handlers.put(handlerConfig.getName(), handlerConfig);
    }

}
