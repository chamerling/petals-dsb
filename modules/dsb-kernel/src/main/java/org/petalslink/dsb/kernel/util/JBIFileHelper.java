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
package org.petalslink.dsb.kernel.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
public class JBIFileHelper {

    private JBIFileHelper() {
    }

    public static Jbi readDescriptor(File file) {
        Jbi result = null;
        try {

            final ZipFile zipFile = new ZipFile(file);
            final ZipEntry jbiDescriptorZipEntry = zipFile.getEntry("META-INF/jbi.xml");
            final InputStream jbiDescriptorInputStream = zipFile
                    .getInputStream(jbiDescriptorZipEntry);

            // Load the JBI descriptor
            result = JBIDescriptorBuilder.buildJavaJBIDescriptor(jbiDescriptorInputStream);

        } catch (JBIDescriptorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
