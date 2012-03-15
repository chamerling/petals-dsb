/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.outgoing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class SOAPCallerTest {

    /**
     * Creates a new instance of SOAPCallerTest
     * 
     * @param name
     */
    public SOAPCallerTest() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFilter() throws Exception {
        SOAPCaller caller = new SOAPCaller(null, null);
        List<String> filters = new ArrayList<String>();
        filters.add("org.ow2.petals.test");
        filters.add("org.ow2.petals.junit");

        assertTrue(caller.isFilteredValue("org.ow2.petals.test", filters));
        assertTrue(caller.isFilteredValue("org.ow2.petals.junit", filters));
        assertFalse(caller.isFilteredValue("org.ow2.petals.ch.ham", filters));
        filters.add("org.ow2.petals.ch*");
        assertTrue(caller.isFilteredValue("org.ow2.petals.ch.ham", filters));
    }

}
