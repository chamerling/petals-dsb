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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;

import org.ow2.petals.jmx.JMXClient;
import org.ow2.petals.jmx.exception.ConnectionErrorException;
import org.ow2.petals.jmx.exception.PetalsAdminDoesNotExistException;
import org.ow2.petals.jmx.exception.PetalsAdminServiceErrorException;
import org.ow2.petals.kernel.api.server.PetalsException;
import org.ow2.petals.kernel.api.server.PetalsServer;
import org.ow2.petals.kernel.api.server.util.SystemUtil;
import org.ow2.petals.launcher.util.CommandReader;
import org.ow2.petals.launcher.util.Locker;
import org.ow2.petals.launcher.util.SystemExitHook;
import org.ow2.petals.topology.TopologyException;

/**
 * 
 * Created on 29 janv. 08
 * 
 * @author Christophe HAMERLING, Roland Naudin - eBM WebSourcing
 * @since 1.0
 * 
 */
public abstract class AbstractLauncher implements org.petalslink.dsb.launcher.PetalsStateListener, PetalsLauncher {

    protected static final String START_COMMAND = "start";

    protected static final String STOP_COMMAND = "stop";

    protected static final String SHUTDOWN_COMMAND = "shutdown";

    protected static final String VERSION_COMMAND = "version";

    public static final String PROPERTY_CONTAINER_NAME = "petals.container.name";

    /**
     * The Petals server instance
     */
    protected PetalsServer petalsServer;

    /**
     * System exit hook used if the command line is used
     */
    protected SystemExitHook systemExitHook;

    /**
     * The PEtALS locker indicating that PEtALS is running
     */
    private final Locker locker;

    /**
     * The JMX client instance
     */
    protected JMXClient jmxClient;

    /**
     * Default constructor
     */
    public AbstractLauncher() {
        this.locker = new Locker(SystemUtil.getPetalsInstallDirectory());
        this.showBanner();
    }

    /**
     * 
     */
    protected void showBanner() {
        System.out.println();
        System.out.println(" -----------------------------------------------------------");
        System.out.println("|                                                           |");
        System.out.println("|             OW2 PEtALS Enterprise Service Bus             |");
        System.out.println("|                   http://petals.ow2.org                   |");
        System.out.println("|                                                           |");
        System.out.println(" -----------------------------------------------------------");
        System.out.println();
    }

    /**
     * The main program
     * 
     * @param args
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "Dm" }, justification = "System.exit(...) are used in the right place.")
    public void launch(String[] args) {
        String command = null;
        boolean console = false;

        if (args.length == 0) {
            this.printUsage();
            System.exit(-1);
        }

        for (String arg : args) {
            if ("-console".equals(arg)) {
                console = true;
            } else {
                command = arg;
            }
        }

        try {
            if (STOP_COMMAND.equals(command)) {
                System.out.println("PEtALS ESB is stopping...");
                this.stop();

            } else if (SHUTDOWN_COMMAND.equals(command)) {
                System.out.println("PEtALS ESB is shutting down...");
                this.shutdown();

            } else if (VERSION_COMMAND.equals(command)) {
                this.version();

            } else if (START_COMMAND.equals(command)) {
                System.out.println("PEtALS ESB is starting...");
                this.start();

                // show the commandLine mode if asked
                if (console) {
                    this.commandLineMode();
                }

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
            throw new PetalsException(
                    "Can not start the PEtALS server, remove lock file from PEtALS root path or stop server");
        }

        this.petalsServer = this.loadPetalsServer();
        
        // CHA 2012 = don't care
        // this.petalsServer.addPetalsStateListener(this);

        // add a hook if a terminate signal is sent from the command line
        this.systemExitHook = new SystemExitHook(this.petalsServer, this.locker, this);
        Runtime.getRuntime().addShutdownHook(this.systemExitHook);

        this.petalsServer.init();
        this.petalsServer.start();
        System.out.println("System Information : "
                + this.getJMXClient().getAdminServiceClient().getSystemInfo());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.PetalsLauncher#stop()
     */
    public void stop() throws Exception {
        this.getJMXClient().getPetalsAdminServiceClient().stopContainer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.PetalsLauncher#shutdown()
     */
    public void shutdown() throws Exception {
        this.getJMXClient().getPetalsAdminServiceClient().shutdownContainer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.PetalsLauncher#version()
     */
    public void version() throws Exception {
        System.out.println(this.getJMXClient().getAdminServiceClient().getSystemInfo());
    }

    /**
     * Print the launcher usage
     */
    protected void printUsage() {
        System.out.println("usage:");
        System.out.println(" -start [-console]       start the PEtALS container");
        System.out.println(" -stop                   stop the PEtALS container");
        System.out.println(" -shutdown               shutdown the PEtALS container");
        System.out.println(" -version                get the PEtALS container version");
    }

    /**
     * Print the petals welcome message
     */
    private void printStartedMessage() {
        this.printSpecificStartMessage();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat();
        System.out.println();
        System.out.println("PEtALS ESB " + this.getDistributionName()
                + " distribution successfully started - " + sdf.format(date));
    }

    /**
     * To be overrided for specific message
     */
    protected void printSpecificStartMessage() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.petals.kernel.server.PetalsListener#onPetalsStarted()
     */
    public void onPetalsStarted() {
        this.locker.lock();
        this.printStartedMessage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.petals.kernel.server.PetalsListener#onPetalsStopped()
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "Dm" }, justification = "System.exit(...) are used in the right place.")
    public void onPetalsStopped(boolean success, Exception exception) {
        // remove the System Hook to avoid the Petals server stop() called a
        // second time
        Runtime.getRuntime().removeShutdownHook(this.systemExitHook);

        this.locker.unlock();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (success) {
            System.out.println("PEtALS ESB " + this.getDistributionName()
                    + " distribution is stopped - " + sdf.format(date));
            System.exit(0);
        } else {
            exception.printStackTrace();
            System.out.println("PEtALS ESB " + this.getDistributionName()
                    + " distribution is not properly stopped - " + sdf.format(date));
            System.exit(-1);
        }
    }

    /**
     * Get the petals JMX client. This is dependent to the distribution
     * 
     * @return
     * @throws IOException
     * @throws JAXBException
     * @throws TopologyException
     * @throws ConnectionErrorException
     * @throws PetalsAdminDoesNotExistException
     * @throws PetalsAdminServiceErrorException
     * @throws ConfigurationException
     */
    protected abstract JMXClient getJMXClient() throws IOException, JAXBException,
            TopologyException, ConnectionErrorException, PetalsAdminDoesNotExistException,
            PetalsAdminServiceErrorException, ConfigurationException;

    /**
     * Show a command line to interact with Petals. TODO we pass the PetalsAdmin
     * object to the CommandReader to perform some actions. Find a better way.
     * 
     * @throws TopologyException
     * @throws JAXBException
     * @throws IOException
     * @throws PetalsAdminServiceErrorException
     * @throws PetalsAdminDoesNotExistException
     * @throws ConnectionErrorException
     * @throws ConfigurationException
     * @throws NoSuchInterfaceException
     * @throws ADLException
     * @throws PetalsException
     */
    protected void commandLineMode() throws ConnectionErrorException,
            PetalsAdminDoesNotExistException, PetalsAdminServiceErrorException, IOException,
            JAXBException, TopologyException, ConfigurationException {
        CommandReader console = new CommandReader(this.getJMXClient(), this.petalsServer);
        console.read();
    }

    /**
     * Load the PetalsServer instance into its own classLoader.
     * <p>
     * TODO: Find a better way to do get the petals-kernel jar resource
     * 
     * @return The instance of the PetalsServer
     * @throws IOException
     *             The petals-kernel resource has not been found or is invalid
     * @throws ClassNotFoundException
     *             The PetalsServerImpl class has not been found
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     */
    protected PetalsServer loadPetalsServer() throws IOException, ClassNotFoundException,
            SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {

        File petalsKernelFile = this.getBootstrapJAR();

        if (petalsKernelFile == null) {
            throw new IOException("Failed to get the PEtALS bootstrap file");
        }

        ClassLoader petalsKernelClassLoader = new URLClassLoader(new URL[] { petalsKernelFile
                .toURI().toURL() }, AbstractLauncher.class.getClassLoader());
        Class<?> petalsKernelClass = petalsKernelClassLoader
                .loadClass("org.ow2.petals.kernel.server.PetalsServerImpl");
        PetalsServer newPetalsServer = (PetalsServer) petalsKernelClass.newInstance();
        Thread.currentThread().setContextClassLoader(petalsKernelClassLoader);

        return newPetalsServer;
    }

    /**
     * Get the bootstrap JAR. NOTE : This is just an utility while waiting to
     * have a real bootstrap project...
     * 
     * @return
     */
    protected File getBootstrapJAR() {
        File bootstrapFile = null;
        File libDirectory = new File(SystemUtil.getPetalsInstallDirectory() + File.separator
                + "lib");

        for (File file : libDirectory.listFiles()) {
            if (file.getName().matches("petals-kernel-[0-9.]+\\.jar")
                    || file.getName().matches("petals-kernel-[0-9.]+-SNAPSHOT\\.jar")) {
                bootstrapFile = file;
                break;
            }
        }
        return bootstrapFile;
    }

    /**
     * Get the distribution name
     * 
     * @return
     */
    protected abstract String getDistributionName();
}
