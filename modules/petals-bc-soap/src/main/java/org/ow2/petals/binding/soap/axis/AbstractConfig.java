package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class AbstractConfig {

    private final Map<String, String> parameters;
    
    public AbstractConfig() {
        this.parameters = new LinkedHashMap<String, String>();
    }
    
    public final void addParameter(String parameterName, String parameterValue) {
        assert parameterName != null;
        assert parameterValue != null;
        parameters.put(parameterName, parameterValue);
    }
    
    
    protected final Map<String, String> getParameters() {
        return parameters;
    }
    
    public abstract void dump(Writer writer) throws IOException;
    
}
