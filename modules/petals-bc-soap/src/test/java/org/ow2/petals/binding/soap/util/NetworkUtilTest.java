/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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
package org.ow2.petals.binding.soap.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class NetworkUtilTest extends TestCase {

    public void testIsLocalAddress() throws Exception {
//        InetAddress address = InetAddress.getByName("localhost");
//        assertTrue("localhost is not recognized as local address", NetworkUtil
//                .isLocalAddress(address));
//        
//        address = InetAddress.getLocalHost();
//        assertTrue("InetAddress.getLocalHost() is not recognized as local address", NetworkUtil
//                .isLocalAddress(address));
//        
//        address = InetAddress.getByName("java.sun.com");
//        assertFalse("java.sun.com is recognized as local address", NetworkUtil
//                .isLocalAddress(address));
//
//        InetAddress[] addresses = InetAddress.getAllByName(null);
//        for (InetAddress inetAddress : addresses) {
//            assertTrue(inetAddress.getHostName() + " is not recognized as local address",
//                    NetworkUtil.isLocalAddress(inetAddress));
//        }
    }

    public void testIsLoopbackAddress() throws Exception {
        System.out.println("Test for 'localhost'");
        InetAddress address = InetAddress.getByName("localhost");
        assertTrue("localhost is not recognized as a loopback address", NetworkUtil
                .isLoopbackAddress(address));

        System.out.println("Test for '127.0.0.1'");
        address = InetAddress.getByName("127.0.0.1");
        assertTrue("127.0.0.1 is not recognized as a loopback address", NetworkUtil
                .isLoopbackAddress(address));

        address = InetAddress.getByName("google.com");
        System.out.println("Test for 'google.com' : " + address);
        assertFalse("google.com is not recognized as a loopback address", NetworkUtil
                .isLoopbackAddress(address));
    }

    /**
     * No assert, just a test
     */
    public void testGetAllLocalInterfaces() {
        System.out.println("Get all the network interfaces which are not loopback ones : ");
        Set<NetworkInterface> set = NetworkUtil.getAllLocalInterfaces();
        for (NetworkInterface networkInterface : set) {
            System.out.println(networkInterface);
        }
    }
}
