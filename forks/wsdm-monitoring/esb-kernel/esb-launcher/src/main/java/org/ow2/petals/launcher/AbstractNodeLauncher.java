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

package org.ow2.petals.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.launcher.util.CommandReader;
import org.ow2.petals.launcher.util.Locker;
import org.ow2.petals.launcher.util.SystemExitHook;

/**
 * 
 * Created on 29 janv. 08
 * 
 * @author Christophe HAMERLING, Roland Naudin - eBM WebSourcing
 * @since 1.0
 * 
 */
public abstract class AbstractNodeLauncher implements Launcher {

	private static final String START_COMMAND = "start";

	private static final String SHUTDOWN_COMMAND = "shutdown";

	private static final String VERSION_COMMAND = "version";

	protected ESBKernelFactory factory = null;

	private final static String LOGGER_FILE = "logger.properties";

	static {
		try {
			InputStream inputStream = null;
			File logger = new File(LOGGER_FILE);
			if (logger.exists()) {
				inputStream = new FileInputStream(logger);
			} else {
				// ty to get from classpath
				inputStream = AbstractNodeLauncher.class.getClass()
						.getResourceAsStream("/" + LOGGER_FILE);
			}
			if (inputStream != null) {
				LogManager.getLogManager().readConfiguration(inputStream);
			}
		} catch (final Exception e) {
			throw new RuntimeException("couldn't initialize logging properly",
					e);
		}
	}

	/**
	 * The Petals server instance
	 */
	private Node petalsServer;

	/**
	 * System exit hook used if the command line is used
	 */
	private SystemExitHook systemExitHook;

	/**
	 * The PEtALS locker indicating that PEtALS is running
	 */
	private final Locker locker;

	/**
	 * Default constructor
	 */
	public AbstractNodeLauncher() {
		this.locker = new Locker(new File("."));
		this.showBanner();
	}

	/**
     * 
     */
	protected void showBanner() {
		System.out.println();
		System.out
				.println(" -----------------------------------------------------------");
		System.out
				.println("|                                                           |");
		System.out
				.println("|             EBM Research Enterprise Service Bus           |");
		System.out
				.println("|                   http://petals.ow2.org                   |");
		System.out
				.println("|                                                           |");
		System.out
				.println(" -----------------------------------------------------------");
		System.out.println();
	}

	/**
	 * The main program
	 * 
	 * @param args
	 */
	public void launch(String[] args) {
		List<String> command = new ArrayList<String>();
		boolean console = true;

		if (args.length == 0) {
			this.printUsage();
			System.exit(-1);
		}

		for (String arg : args) {
			command.add(arg);
		}

		try {
			if (command.contains(SHUTDOWN_COMMAND)) {
				System.out.println("ESB is stopping...");
				this.shutdown();

			} else if (command.contains(VERSION_COMMAND)) {
				this.version();

			} else if (command.contains(START_COMMAND)) {
				System.out.println("ESB is starting...");
				this.start();

				// show the commandLine mode if asked
				if (console) {
					this.commandLineMode(this.petalsServer);
				}
				if (this.systemExitHook != null) {
					Runtime.getRuntime()
							.removeShutdownHook(this.systemExitHook);
				}
				this.systemExitHook.run();

				System.exit(0);
			} else {
				System.out.println("Command '" + command + "' is unknown");
				this.printUsage();
				System.exit(-1);
			}
		} catch (Throwable e) {
			System.out.println("Command processing error : " + command);
			e.printStackTrace(System.err);
			if (this.systemExitHook != null) {
				Runtime.getRuntime().removeShutdownHook(this.systemExitHook);
			}
			System.exit(-1);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.petals.launcher.PetalsLauncher#start()
	 */
	public void start() throws Exception {
		// check if started or not stopped correctly
		if (this.locker.isLocked()) {
			throw new ESBException(
					"Can not start the ESB server, remove lock file from PEtALS root path or stop server");
		}

		this.locker.lock();

		this.petalsServer = this.getFactory().createNode(
				new QName("http://ow2.petals.org", "node0"), false);

		// add a hook if a terminate signal is sent from the command
		// line
		this.systemExitHook = new SystemExitHook(this.locker);
		Runtime.getRuntime().addShutdownHook(this.systemExitHook);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.petals.launcher.PetalsLauncher#stop()
	 */
	public void shutdown() throws Exception {
		this.systemExitHook.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.petals.launcher.PetalsLauncher#version()
	 */
	public void version() throws Exception {
		System.out.println("No version");
	}

	/**
	 * Print the launcher usage
	 */
	protected void printUsage() {
		System.out.println("usage:");
		System.out
				.println(" -start                  start the PEtALS container");
		System.out
				.println(" -shutdowm               shutdown the PEtALS container");
		System.out
				.println(" -version                get the PEtALS container version");
	}

	/**
	 * Print the petals welcome message
	 */
	private void printStartedMessage() {
		this.printSpecificStartMessage();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat();
		System.out.println();
		System.out.println("ESB " + this.getDistributionName()
				+ " distribution successfully started - " + sdf.format(date));
	}

	/**
	 * To be overrided for specific message
	 */
	protected void printSpecificStartMessage() {
	}

	/**
	 * Show a command line to interact with Petals. TODO we pass the PetalsAdmin
	 * object to the CommandReader to perform some actions. Find a better way.
	 * 
	 * @param node
	 * 
	 * @throws Exception
	 */
	protected void commandLineMode(Node node) throws Exception {
		CommandReader console = new CommandReader(node);
		console.read();
	}

	/**
	 * Get the distribution name
	 * 
	 * @return
	 */
	protected abstract String getDistributionName();
}
