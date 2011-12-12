package org.petalslink.dsb.jaxbutils;

public abstract class Framework {
    
    public Framework() {
    }
    
    String getName() {
        return this.getClass().getSimpleName();
    }

   
}
