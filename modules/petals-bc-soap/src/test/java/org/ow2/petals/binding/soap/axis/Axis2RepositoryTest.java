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

package org.ow2.petals.binding.soap.axis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.ebmwebsourcing.easycommons.io.FileSystemHelper;

public class Axis2RepositoryTest {

    private static final Set<String> arrayToSet(String[] strings) {
        Set<String> result = new HashSet<String>();
        for (String s : strings)
            result.add(s);
        return result;
    }

    @Test
    public void testSetUp() throws Exception {
        File baseDir = FileSystemHelper.createTempDir();
        Axis2Repository repository = new Axis2Repository(baseDir);
        repository.setUp();
        assertEquals(arrayToSet(new String[] { Constants.AXIS2_REPOSITORY_MODULES_DIR_NAME,
                Constants.AXIS2_REPOSITORY_SERVICES_DIR_NAME }), arrayToSet(baseDir.list()));
    }

    @Test
    public void testSetUpBaseDirDoesNotExist() throws Exception {
        File baseDir = FileSystemHelper.createTempDir();
        baseDir.delete();
        Axis2Repository repository = new Axis2Repository(baseDir);
        repository.setUp();
        assertEquals(arrayToSet(new String[] { Constants.AXIS2_REPOSITORY_MODULES_DIR_NAME,
                Constants.AXIS2_REPOSITORY_SERVICES_DIR_NAME }), arrayToSet(baseDir.list()));
    }

    @Test
    public void testSetUpBaseDirModulesAndServicesDirAlreadyExist() throws Exception {
        File baseDir = FileSystemHelper.createTempDir();
        File modulesDir = new File(baseDir, Constants.AXIS2_REPOSITORY_MODULES_DIR_NAME);
        assertTrue(modulesDir.mkdir());
        File servicesDir = new File(baseDir, Constants.AXIS2_REPOSITORY_SERVICES_DIR_NAME);
        assertTrue(servicesDir.mkdir());
        Axis2Repository repository = new Axis2Repository(baseDir);
        repository.setUp();
        assertEquals(arrayToSet(new String[] { Constants.AXIS2_REPOSITORY_MODULES_DIR_NAME,
                Constants.AXIS2_REPOSITORY_SERVICES_DIR_NAME }), arrayToSet(baseDir.list()));
    }

}
