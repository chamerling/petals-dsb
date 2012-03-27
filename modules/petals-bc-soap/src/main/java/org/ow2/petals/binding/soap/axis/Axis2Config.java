
package org.ow2.petals.binding.soap.axis;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Axis2Config extends AbstractNamedConfig {

    private final Map<String, MessageReceiverConfig> messageReceivers;

    private final Map<String, TransportReceiverConfig> transportReceivers;

    private final Map<String, TransportSenderConfig> transportSenders;

    private final Map<String, PhaseOrderConfig> phaseOrders;

    public Axis2Config(String name) {
        super(name);
        this.messageReceivers = new LinkedHashMap<String, MessageReceiverConfig>();
        this.transportReceivers = new LinkedHashMap<String, TransportReceiverConfig>();
        this.transportSenders = new LinkedHashMap<String, TransportSenderConfig>();
        this.phaseOrders = new LinkedHashMap<String, PhaseOrderConfig>();
    }

    @Override
    public void dump(Writer writer) throws IOException {
        writer.write(String.format("<axisconfig name='%s'>\n", getName()));
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            writer.write(String.format("<parameter name='%s'>%s</parameter>\n", entry.getKey(),
                    entry.getValue()));
        }
        writer.write("<messageReceivers>\n");
        for (MessageReceiverConfig messageReceiver : messageReceivers.values()) {
            messageReceiver.dump(writer);
        }
        writer.write("</messageReceivers>\n");

        writer.write("<messageFormatters/>\n");

        writer.write("<messageBuilders/>\n");

        for (TransportReceiverConfig transportReceiver : transportReceivers.values()) {
            transportReceiver.dump(writer);
        }

        for (TransportSenderConfig transportSender : transportSenders.values()) {
            transportSender.dump(writer);
        }

        for (PhaseOrderConfig phaseOrder : phaseOrders.values()) {
            phaseOrder.dump(writer);
        }

        writer.write("</axisconfig>");
    }

    public void addMessageReceiver(MessageReceiverConfig messageReceiverConfig) {
        assert messageReceiverConfig != null;
        this.messageReceivers.put(messageReceiverConfig.getMep(), messageReceiverConfig);

    }

    public void addTransportReceiver(TransportReceiverConfig transportReceiverConfig) {
        assert transportReceiverConfig != null;
        this.transportReceivers.put(transportReceiverConfig.getName(), transportReceiverConfig);
    }

    public void addTransportSender(TransportSenderConfig transportSenderConfig) {
        assert transportSenderConfig != null;
        this.transportSenders.put(transportSenderConfig.getName(), transportSenderConfig);
    }

    public void addPhaseOrder(PhaseOrderConfig phaseOrderConfig) {
        assert phaseOrderConfig != null;
        this.phaseOrders.put(phaseOrderConfig.getName(), phaseOrderConfig);
    }

}
