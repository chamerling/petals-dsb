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
package org.ow2.petals.tools.generator.commons;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.ebmwebsourcing.commons.jbi.sugenerator.utils.JbiZipper;

/**
 * @author chamerling - eBM WebSourcing
 *
 */
public class JBIUtils {

    public static File createSUZipFile(File wsdlFile, List<File> suZipFiles, String suName,
            String jbiXml, File outputDir, File workDirectory) throws GeneratorException {
        File suFile = null;
        File jbiSu = null;
        try {
            File tempSuDirectory = new File(workDirectory, suName);
            if (!tempSuDirectory.exists()) {
                tempSuDirectory.mkdirs();
            }

            jbiSu = new File(tempSuDirectory, "jbi.xml");
            if (!jbiSu.exists()) {
                jbiSu.createNewFile();
            }

            FileWriter writer = new FileWriter(jbiSu);
            writer.write(jbiXml);
            writer.close();

            // create the SU
            JbiZipper jbiZipper = JbiZipper.getInstance();
            List<File> rootFiles = new ArrayList<File>();
            rootFiles.add(jbiSu);

            if (wsdlFile != null) {
                rootFiles.add(wsdlFile);
            }

            suFile = new File(outputDir, suName + ".zip");
            suZipFiles.add(jbiZipper.createSuZipFile(suFile, rootFiles));

            jbiSu.delete();

        } catch (Exception e) {
            throw new GeneratorException(e);
        }
        return suFile;
    }

}
