package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceConfig extends AbstractNamedConfig {

    private final List<String> transports;
    private final Map<String, OperationConfig> operations;
    
    
    public ServiceConfig(String name) {
        super(name);
        this.transports = new ArrayList<String>();
        this.operations = new LinkedHashMap<String, OperationConfig>(); 
    }
    
    public void addTransport(String transport) {
        assert transport != null;
        transports.add(transport);
    }
    
    
    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<service name='%s'>\n", getName()));
        writer.write("  <transports>\n");
        for (String transport : transports) {
            writer.write(String.format("  <transport>%s</transport>\n", transport));
        }
        writer.write("  </transports>\n");
        for (OperationConfig operation : operations.values()) {
            operation.dump(writer);
        }
        writer.write("</service>\n");
    }

    public void addOperation(OperationConfig operationConfig) {
        assert operationConfig != null;
        operations.put(operationConfig.getName(), operationConfig);
    }

}
