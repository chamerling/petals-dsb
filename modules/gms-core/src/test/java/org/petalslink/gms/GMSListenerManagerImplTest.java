package org.petalslink.gms;

import org.petalslink.gms.GMSMessage.Type;

import junit.framework.TestCase;

public class GMSListenerManagerImplTest extends TestCase {

    GMSListenerManager manager;

    @Override
    protected void setUp() throws Exception {
        manager = new GMSListenerManagerImpl();
    }

    @Override
    protected void tearDown() throws Exception {
        this.manager = null;
    }

    public void testRegister() {
        assertEquals(manager.getListeners().size(), 0);
        manager.register(new GMSListener() {

            public void onMessage(GMSMessage message) {
                // TODO Auto-generated method stub

            }

        });
        assertEquals(manager.getListeners().size(), 1);
    }

    public void testGetListeners() {
        manager.register(new GMSListener() {

            public void onMessage(GMSMessage message) {
                // TODO Auto-generated method stub

            }

        });
        assertEquals(manager.getListeners().size(), 1);
        manager.register(new GMSListener() {

            public void onMessage(GMSMessage message) {
                // TODO Auto-generated method stub

            }

        });
        assertEquals(manager.getListeners().size(), 2);
    }

    public void testUnregister() {
        GMSListener listener = new GMSListener() {

            public void onMessage(GMSMessage message) {
                // TODO Auto-generated method stub

            }

        };
        manager.register(listener);
        assertEquals(manager.getListeners().size(), 1);
        manager.unregister(listener);
        assertEquals(manager.getListeners().size(), 0);
    }

}
