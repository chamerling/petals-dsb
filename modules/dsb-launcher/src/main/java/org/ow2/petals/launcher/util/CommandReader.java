/**
 * PETALS - PETALS Services Platform. Copyright (c) 2005 EBM Websourcing,
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
 * $Id: CommandReader.java 1:04:18 PM alouis $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.ow2.petals.jmx.JMXClient;
import org.ow2.petals.jmx.PetalsAdminServiceClient;
import org.ow2.petals.jmx.exception.ConnectionErrorException;
import org.ow2.petals.jmx.exception.PetalsAdminDoesNotExistException;
import org.ow2.petals.jmx.exception.PetalsAdminServiceErrorException;
import org.ow2.petals.kernel.api.server.PetalsServer;
import org.ow2.petals.kernel.api.server.util.SystemUtil;
import org.ow2.petals.launcher.tasks.EndpointListTask;
import org.ow2.petals.launcher.tasks.GetComponentsTask;
import org.ow2.petals.launcher.tasks.GetCurrentPathTask;
import org.ow2.petals.launcher.tasks.HotDeployTask;
import org.ow2.petals.launcher.tasks.HotUndeployTask;
import org.ow2.petals.launcher.tasks.InfoTask;
import org.ow2.petals.launcher.tasks.JNDITask;
import org.ow2.petals.launcher.tasks.SetPathTask;
import org.ow2.petals.launcher.tasks.ShutdownTask;
import org.ow2.petals.launcher.tasks.StopTask;
import org.ow2.petals.launcher.tasks.SynchronizeRegistryTask;
import org.ow2.petals.launcher.tasks.Task;

/**
 * Used to read command from the console
 * 
 * @author alouis - eBMWebsourcing
 */
public class CommandReader {

    protected File currentPath;

    protected File installPath;

    protected File installedPath;

    protected File uninstalledPath;

    protected File repositoryPath;

    protected PetalsAdminServiceClient petalsService;

    protected PetalsServer petalsServer;

    protected JMXClient client;

    SortedMap<String, Task> map = new TreeMap<String, Task>();

    /**
     * 
     * @param petalsService
     * @param petalsServer
     * @throws ConnectionErrorException
     * @throws PetalsAdminServiceErrorException
     * @throws PetalsAdminDoesNotExistException
     */
    public CommandReader(JMXClient client, PetalsServer petalsServer)
            throws PetalsAdminDoesNotExistException, PetalsAdminServiceErrorException,
            ConnectionErrorException {
        this.currentPath = new File(".");

        File petalsPath = SystemUtil.getPetalsInstallDirectory();
        // FIXME : get from kernel API
        this.installPath = new File(petalsPath, "install");
        this.installedPath = new File(petalsPath, "installed");
        this.repositoryPath = new File(petalsPath, "repo");

        this.client = client;
        this.petalsService = client.getPetalsAdminServiceClient();
        this.petalsServer = petalsServer;

        // create the commands
        this.addCommand(new GetCurrentPathTask(this.currentPath));
        this.addCommand(new HotDeployTask(this.currentPath, this.installPath));
        this.addCommand(new HotUndeployTask(this.installedPath));
        this.addCommand(new InfoTask(this.client, this.petalsServer));
        this.addCommand(new JNDITask(this.petalsServer));
        this.addCommand(new SetPathTask(this.currentPath));
        this.addCommand(new ShutdownTask(this.petalsService));
        this.addCommand(new StopTask(this.petalsService));
        this.addCommand(new GetComponentsTask(this.client));
        this.addCommand(new EndpointListTask(this.petalsServer));
        this.addCommand(new SynchronizeRegistryTask(this.petalsServer));
        // TODO : uncomment the tasks when the local repository would be
        // finished
        // addCommand(new GetRepositoryContentTask(this.repositoryPath));
        // addCommand(new InstallFromRepositoryTask(this.repositoryPath,
        // installPath));
    }

    /**
     * 
     * @param command
     */
    private void addCommand(Task command) {
        this.map.put(command.getShortcut(), command);
    }

    /**
     * Read command line. This method ends with an "exit" command line
     * 
     */
    public void read() {
        System.out.println("PEtALS prompt. Tape 'help' for help.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int code = 1;

        while (code > 0) {
            try {
                System.out.println();
                System.out.print("petals@localhost:/> ");
                code = this.processCommandLine(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Process the command, and return a code if "exit" command, return -1
     * 
     * @param command
     * @return
     */
    protected int processCommandLine(String command) {
        int result = Task.OK_CODE;
        if ((command != null) && (command.length() > 0)) {
            StringTokenizer tokenizer = new StringTokenizer(command, " ");
            List<String> args = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                args.add(tokenizer.nextToken());
            }

            String key = args.get(0);
            if ("h".equalsIgnoreCase(key) || "help".equalsIgnoreCase(key)) {
                this.printUsage();
            } else {
                // get the key for the requested task
                Task task = this.getTask(key);
                if (task == null) {
                    System.out
                            .println("Unrecognized command : " + command + " (type 'h' for help)");
                } else {
                    // get the command arguments
                    List<String> arguments = null;
                    if (args.size() > 1) {
                        arguments = args.subList(1, args.size());
                    } else {
                        arguments = new ArrayList<String>(0);
                    }
                    result = task.process(arguments);
                    if (result == Task.INVALID_ARGS) {
                        System.out.println("Bad arguments : " + arguments + " (type 'h' for help)");
                        result = Task.OK_CODE;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Print the available commands and their options
     * 
     */
    private void printUsage() {
        System.out.println("PEtALS prompt usage:");
        Iterator<String> iter = this.map.keySet().iterator();
        while (iter.hasNext()) {
            Task command = this.map.get(iter.next());
            System.out.println(" - " + command.toString());
        }
    }

    /**
     * Return the task from its shortcut or name.
     * 
     * @param command
     * @return null if no task has been found
     */
    private Task getTask(String command) {
        Task task = this.map.get(command);

        if (task == null) {
            // try to get shortcut from command
            String shortcut = this.getShortcutFromCommand(command);
            if (shortcut != null) {
                task = this.map.get(shortcut);
            }
        }
        return task;
    }

    /**
     * 
     * @param command
     * @return
     */
    private String getShortcutFromCommand(String command) {
        String result = null;
        boolean found = false;

        Iterator<String> iter = this.map.keySet().iterator();
        while (iter.hasNext() && !found) {
            Task task = this.map.get(iter.next());
            if (command.equals(task.getName())) {
                result = task.getShortcut();
                found = true;
            }
        }
        return result;
    }
}
