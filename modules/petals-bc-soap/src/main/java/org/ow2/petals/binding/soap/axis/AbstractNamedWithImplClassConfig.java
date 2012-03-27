package org.ow2.petals.binding.soap.axis;


abstract class AbstractNamedWithImplClassConfig extends AbstractNamedConfig {

    private final Class<?> implClass;
    
    protected AbstractNamedWithImplClassConfig(String name, Class<?> implClass) {
        super(name);
        this.implClass = implClass;
    }
    
    protected Class<?> getImplClass() {
        return implClass;
    }
    
}
