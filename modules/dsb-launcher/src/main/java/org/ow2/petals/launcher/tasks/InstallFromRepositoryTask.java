/*
 * Copyright (c) 2009 EBM Websourcing, http://www.ebmwebsourcing.com/
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
 */
package org.ow2.petals.launcher.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.jmx.JMXClient;

/**
 * Install a component from the repository. Use it like this for example 'ri
 * petals-bc-soap-3.2' (component name without file extension).
 * 
 * FIXME : Use {@link JMXClient} instead of file copy!
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class InstallFromRepositoryTask extends Task {

    private final File repository;

    private final File installPath;

    /**
     * 
     * @param repositoryPath
     * @param installPath
     */
    public InstallFromRepositoryTask(final File repositoryPath, final File installPath) {
        this.setDescription("Install artifact from the repository");
        this.setName("repoinstall");
        this.setShortcut("ri");

        this.repository = repositoryPath;
        this.installPath = installPath;
    }

    @Override
    protected int doProcess(List<String> args) {
        // for now just do it by copying things in the install path... need to
        // use the PEtALS API...
        String componentToInstall = args.get(0);
        File[] files = this.repository.listFiles(new ComponentNamesFilter());

        if (files == null) {
            System.out.println("No file in repository");
            return ERROR_CODE;
        }

        for (File file : files) {
            String name = this.getComponentName(file);
            if (name.equals(componentToInstall)) {
                System.out.println("Installing component " + componentToInstall + "...");
                // do it in a separate thread
                FutureTask<String> task = new FutureTask<String>(new FutureInstaller(file,
                        this.installPath));
                Thread t = new Thread(task);
                t.setDaemon(true);
                t.start();

                try {
                    System.out.println(task.get(20, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    System.out.println("Timeout while installing component");
                }
                break;
            }
        }

        return OK_CODE;
    }

    @Override
    public boolean validateArgs(List<String> args) {
        return (args != null) && (args.size() > 0);
    }

    /**
     * Get the component name from the file. TODO : Get it from the jbi.xml...
     * 
     * @param file
     * @return
     */
    private String getComponentName(File file) {
        return file.getName().substring(0, file.getName().indexOf(".zip"));
    }

    private class ComponentNamesFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".zip");
        }
    }

    private class FutureInstaller implements Callable<String> {

        private final File source;

        private final File outpath;

        public FutureInstaller(File source, File outpath) {
            this.source = source;
            this.outpath = outpath;
        }

        public String call() throws Exception {
            try {
                FileUtils.copyFileToDirectory(this.source, this.outpath);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw e;
            }
            return "File successfully copied to " + this.outpath;
        }

    }
}
