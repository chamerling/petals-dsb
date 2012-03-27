package org.ow2.petals.binding.soap.axis;

abstract class AbstractNamedConfig extends AbstractConfig {

    private final String name;
    
    protected AbstractNamedConfig(String name) {
        assert name != null;
        this.name = name;
    }
    
    protected String getName() {
        return name;
    }
    
}
