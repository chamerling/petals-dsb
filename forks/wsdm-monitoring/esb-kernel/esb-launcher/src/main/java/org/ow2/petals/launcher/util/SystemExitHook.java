/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2005 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $Id: SystemExitHook.java 1:04:18 PM ddesjardins $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.launcher.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hook used to stop Petals on CTRL-C, stops signals except KILL or
 * System.exit()
 * 
 * @author alouis, ddesjardins , rnaudin - eBMWebsourcing
 */
public class SystemExitHook extends Thread {

	/**
	 * The timeout for stopping petals
	 */
	private static final long STOP_TIMEOUT = 15000;

	private Locker locker;



	/**
	 * 
	 * @param petalsServer
	 */
	public SystemExitHook(Locker locker) {
		super();
		this.locker = locker;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		final SimpleDateFormat sdf = new SimpleDateFormat();
		try {
			System.out.println("ESB is stopping...");

			final Date date = new Date(System.currentTimeMillis());


			System.out.println("ESB container is properly stopped - "
					+ sdf.format(date));


		} catch (Throwable e) {
			e.printStackTrace(System.err);
			final Date date = new Date(System.currentTimeMillis());
			System.out.println("ESB container is not properly stopped - "
					+ sdf.format(date) + ": " + e.getMessage());
		} finally {
			if (this.locker.isLocked()) {
				this.locker.unlock();
			}
		}
	}

}
