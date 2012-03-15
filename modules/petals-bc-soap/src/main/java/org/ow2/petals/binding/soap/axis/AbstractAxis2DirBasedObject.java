package org.ow2.petals.binding.soap.axis;

import java.io.File;

abstract class AbstractAxis2DirBasedObject {

    private final File baseDir;
    private boolean isSetUp;

    public AbstractAxis2DirBasedObject(File baseDir) {
        this.baseDir = baseDir;
        this.isSetUp = false;
    }

    protected final File getBaseDir() {
        return baseDir;
    }
    
    protected void createDirIfNeeded(File dir) {
        dir.mkdir();
        assert dir.exists();
    }

    protected final boolean isSetUp() {
        return isSetUp;
    }
    
    public final void setUp() {
        createDirIfNeeded(baseDir);
        specificSetUp();
        isSetUp = true;
    }

    protected abstract void specificSetUp();

}
