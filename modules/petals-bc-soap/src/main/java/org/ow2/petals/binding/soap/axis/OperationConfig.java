package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class OperationConfig extends AbstractNamedConfig {

    private final List<String> actionMappings;
    
    
    public OperationConfig(String name) {
        super(name);
        this.actionMappings = new ArrayList<String>();
    }
    
    public void addActionMapping(String actionMapping) {
        assert actionMapping != null;
        actionMappings.add(actionMapping);
    }

    
    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<operation name='%s'>\n", getName()));
        for (String actionMapping : actionMappings) {
            writer.write(String.format("<actionMapping>%s</actionMapping>\n", actionMapping));
        }
        writer.write(String.format("</operation>\n"));
    }

}
