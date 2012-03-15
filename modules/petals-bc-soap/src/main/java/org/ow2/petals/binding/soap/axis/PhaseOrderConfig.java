
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class PhaseOrderConfig extends AbstractNamedConfig {

    private final Map<String, PhaseConfig> phases;

    public PhaseOrderConfig(String name) {
        super(name);
        this.phases = new LinkedHashMap<String, PhaseConfig>();
    }

    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<phaseOrder type='%s'>\n", getName()));
        for (PhaseConfig phase : phases.values()) {
            phase.dump(writer);
        }
        writer.write("</phaseOrder>");
    }

    public void addPhase(PhaseConfig phaseConfig) {
        phases.put(phaseConfig.getName(), phaseConfig);
    }

}
