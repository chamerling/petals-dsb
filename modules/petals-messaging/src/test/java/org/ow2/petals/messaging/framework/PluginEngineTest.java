/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.messaging.framework;

import java.util.List;

import junit.framework.TestCase;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class PluginEngineTest extends TestCase {

    public void testAddNullPlugin() {
        Engine engine = EngineFactory.getEngine();
        try {
            engine.addComponent(null, null);
            fail();
        } catch (EngineException e) {
        }
    }

    public void testGetUnknowPlugin() {
        Engine engine = EngineFactory.getEngine();
        String plugin = engine.getComponent(String.class);
        assertNull(plugin);
    }

    public void testGetPlugin() {
        Engine engine = EngineFactory.getEngine();
        try {
            engine.addComponent(String.class, "This is a dummy plugin");
            engine.addComponent(Integer.class, 1);
        } catch (EngineException e) {
            fail();
        }

        String plugin = engine.getComponent(String.class);
        assertNotNull(plugin);

        Long lplugin = engine.getComponent(Long.class);
        assertNull(lplugin);
    }

    public void testGetPluginsFromSameInterface() throws Exception {
        Engine engine = EngineFactory.getEngine();

        engine.addComponent(FooPlugin.class, new FooPlugin());
        engine.addComponent(FooFooPlugin.class, new FooFooPlugin());
        engine.addComponent(BarPlugin.class, new BarPlugin());

        List<Foo> fooPlugins = engine.getComponents(Foo.class);
        assertEquals(2, fooPlugins.size());

        List<Bar> barPlugins = engine.getComponents(Bar.class);
        assertEquals(1, barPlugins.size());

        engine.addComponent(FooBarPlugin.class, new FooBarPlugin());
        fooPlugins = engine.getComponents(Foo.class);
        assertEquals(3, fooPlugins.size());

        barPlugins = engine.getComponents(Bar.class);
        assertEquals(2, barPlugins.size());

    }

    interface Foo {
        void bar();
    }

    interface Bar {
        /**
         * 
         */
        void foo();
    }

    class FooPlugin implements Foo {
        /**
         * {@inheritDoc}
         */
        public void bar() {
            // TODO Auto-generated method stub

        }
    }

    class FooFooPlugin implements Foo {
        /**
         * {@inheritDoc}
         */
        public void bar() {
            // TODO Auto-generated method stub
        }
    }

    class BarPlugin implements Bar {
        /**
         * {@inheritDoc}
         */
        public void foo() {
            // TODO Auto-generated method stub

        }
    }

    class FooBarPlugin implements Foo, Bar {
        /**
         * {@inheritDoc}
         */
        public void foo() {
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        public void bar() {
            // TODO Auto-generated method stub

        }
    }
}
