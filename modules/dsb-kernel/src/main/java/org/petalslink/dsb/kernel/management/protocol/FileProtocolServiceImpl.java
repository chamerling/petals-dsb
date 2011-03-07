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
package org.petalslink.dsb.kernel.management.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;

/**
 * A simple protocol service based on a properties file. This properties file
 * contains all the protocols which have to be used when a new endpoint needs to
 * be exposed...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ProtocolService.class) })
public class FileProtocolServiceImpl implements ProtocolService {

    private static final String FILE_NAME = "protocol.properties";

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    /**
     * For now key and value are the protocol
     */
    private Properties props;

    private File protocols;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");

        File configPath = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "conf");
        this.protocols = new File(configPath, FILE_NAME);
        if (!this.protocols.exists()) {
            try {
                this.protocols.createNewFile();
            } catch (IOException e) {
            }
        }

        this.props = new Properties();
        try {
            this.props.load(new FileInputStream(this.protocols));
        } catch (IOException e) {
            this.log.warning("Can not load protocols : " + e.getMessage());
        }

    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");

        try {
            this.props.store(new FileOutputStream(this.protocols), "Saved on " + new Date());
        } catch (IOException e) {
            this.log.warning("Can not save protocols : " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addProtocol(String protocolName) {
        if (this.props.get(protocolName) == null) {
            this.props.put(protocolName, protocolName);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getProtocols() {
        List<String> result = new ArrayList<String>();
        for (Object o : this.props.keySet()) {
            result.add(o.toString());
        }
        return result;
    }

}
