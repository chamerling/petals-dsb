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
package org.petalslink.dsb.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.ow2.petals.kernel.api.server.PetalsServer;
import org.ow2.petals.kernel.api.server.util.SystemUtil;
import org.ow2.petals.launcher.AbstractLauncher;
import org.ow2.petals.launcher.PlatformLauncher;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class GenericPetalsLauncher extends PlatformLauncher {

    private static final String CONFIG = "launcher.cfg";

    private static final String NAME = "name";

    private static final String URL = "url";

    private static final String JARS = "jars";

    private static final String CLASS = "class";

    private static final String MAIL = "mail";

    private static Properties props;

    static {
        File conf = new File(SystemUtil.getPetalsInstallDirectory(), "conf");
        File configFile = new File(conf, CONFIG);
        if (configFile.exists() && configFile.isFile()) {
            props = new Properties();
            try {
                props.load(new FileInputStream(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    public GenericPetalsLauncher() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDistributionName() {
        return (props.get(NAME) != null ? props.get(NAME).toString() : "Petals DSB");
    }

    protected File getJAR(String name) {
        File jarFile = null;
        File libDirectory = new File(SystemUtil.getPetalsInstallDirectory() + File.separator
                + "lib");

        for (File file : libDirectory.listFiles()) {
            if (file.getName().matches(name + "-[0-9.]+\\.jar")
                    || file.getName().matches(name + "-[0-9.]+-SNAPSHOT\\.jar")) {
                jarFile = file;
                break;
            }
        }
        return jarFile;
    }

    @Override
    protected PetalsServer loadPetalsServer() throws IOException, ClassNotFoundException,
            SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {

        // get the extra jars from the launcher configuration file, this file
        // must at least contain the main kernek JAR.
        List<URL> jars = this.getJarUrls(this.getJars());
        if (jars.size() == 0) {
            throw new IOException("Failed to get the PEtALS DSB bootstrap files");
        }

        // let's add some JARs from the ext folder...
        List<URL> exts = getExtraLibs();
        if (exts != null && exts.size() > 0) {
            jars.addAll(exts);
        }

        ClassLoader petalsKernelClassLoader = new URLClassLoader(
                jars.toArray(new URL[jars.size()]), AbstractLauncher.class.getClassLoader());

        Class<?> petalsKernelClass = petalsKernelClassLoader.loadClass(this.getMainServerClass());
        PetalsServer newPetalsServer = (PetalsServer) petalsKernelClass.newInstance();
        Thread.currentThread().setContextClassLoader(petalsKernelClassLoader);

        return newPetalsServer;
    }

    /**
     * @return
     */
    protected List<URL> getExtraLibs() {
        File libDirectory = new File(SystemUtil.getPetalsInstallDirectory(), "lib");
        File extFolder = new File(libDirectory, "ext");
        List<URL> exts = this.getJarUrls(extFolder);
        return exts;
    }

    private List<java.net.URL> getJarUrls(File extFolder) {
        List<URL> result = null;
        if (extFolder != null && extFolder.isDirectory()) {
            result = new ArrayList<URL>();
            File[] jars = extFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches("\\.jar");
                }
            });
            if (jars != null) {
                for (File jar : jars) {
                    if (jar.exists() && !jar.isDirectory()) {
                        try {
                            result.add(jar.toURI().toURL());
                        } catch (MalformedURLException e) {
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return
     */
    private String getJars() {
        return props.getProperty(JARS) != null ? props.getProperty(JARS) : "";
    }

    /**
     * @return
     */
    private String getMainServerClass() {
        return (props.get(CLASS) != null ? props.get(CLASS).toString().trim() : null);
    }

    private List<URL> getJarUrls(String csvValue) throws FileNotFoundException, IOException,
            MalformedURLException {
        List<URL> jars = new ArrayList<URL>();
        StringTokenizer tokenizer = new StringTokenizer(csvValue, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            File f = this.getJAR(token);
            if ((f != null) && f.exists() && !f.isDirectory()) {
                jars.add(f.toURI().toURL());
            } else {
                System.err.println("JAR file '" + token
                        + "' has not been found and will not be added to parent classloader");
            }
        }
        return jars;
    }

    @Override
    protected void showBanner() {
        String title = this.getDistributionName();
        String url = this.getURL();
        String mail = this.getMail();

        int longer = title.length() > url.length() ? title.length() : url.length();
        longer = mail.length() > longer ? mail.length() : longer;
        int delta = 14;
        int size = longer + 2 * delta;
        String line = this.buildTopLine(size);
        String empty = this.buildEmptyLine(size);

        System.out.println();
        System.out.println(line);
        System.out.println(empty);
        System.out.println(this.buildTextLine(title, size));
        System.out.println(this.buildTextLine(url, size));
        System.out.println(this.buildTextLine(mail, size));
        System.out.println(empty);
        System.out.println(line);
        System.out.println();
    }

    /**
     * @param title
     * @param size
     * @return
     */
    private String buildTextLine(String text, int size) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("|");
        int pre = (size - text.length()) / 2;
        for (int i = 0; i < pre; i++) {
            buffer.append(" ");
        }
        buffer.append(text);
        int remain = size - pre - text.length();
        for (int i = 0; i < remain; i++) {
            buffer.append(" ");
        }
        buffer.append("|");
        return buffer.toString();
    }

    /**
     * @return
     */
    private String getURL() {
        return props.getProperty(URL) != null ? props.getProperty(URL).trim() : "";
    }

    private String getMail() {
        return props.getProperty(MAIL) != null ? props.getProperty(MAIL).trim() : "";
    }

    private String buildTopLine(int length) {
        StringBuffer sb = new StringBuffer();
        sb.append(" ");
        for (int i = 0; i < length; i++) {
            sb.append("-");
        }
        sb.append(" ");
        return sb.toString();
    }

    private String buildEmptyLine(int length) {
        StringBuffer sb = new StringBuffer();
        sb.append("|");
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
        sb.append("|");
        return sb.toString();
    }

}
