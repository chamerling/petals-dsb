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
import java.util.List;

/**
 * List the component repository content
 * 
 * TODO : This is temporary implementation, it should use the PEtALS API to do
 * that (work in progress)
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class GetRepositoryContentTask extends Task {

    private File repository;

    public GetRepositoryContentTask(final File repository) {
        this.repository = repository;

        this.setDescription("Display the repository content");
        this.setName("repositorycontent");
        this.setShortcut("rc");
    }

    @Override
    protected int doProcess(List<String> args) {

        // list all the components...
        if (repository != null && repository.exists()) {
            File[] files = repository.listFiles(new ComponentNamesFilter());
            if (files != null && files.length > 0) {
                for (File file : files) {
                    // assume that the component name is component-version.zip
                    // or
                    // component.zip
                    // TODO : need to work on component descriptors...
                    System.out.println(" - " + file.getName());
                }
            } else {
                System.out.println("No components in the repository !");
            }
        } else {
            System.out.println("No repository found !");
        }

        return OK_CODE;
    }

    private class ComponentNamesFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".zip");
        }
    }

}
