package org.petalslink.dsb.kernel.resources.service.utils;

public abstract class Framework {
    
    public Framework() {
    }
    
    String getName() {
        return this.getClass().getSimpleName();
    }

   
}
