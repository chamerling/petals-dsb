package org.petalslink.dsb.bpmn.deployer.server;

import java.io.File;
import java.util.List;

import com.ebmwebsourcing.bpmn.deployer.client.exception.ProcessExecutorServiceException;
import com.ebmwebsourcing.bpmn.deployer.client.to.RunningProcess;
import com.ebmwebsourcing.bpmn.deployer.server.ProcessExecutorService;

public class PetalsDSBBPELService implements ProcessExecutorService {

    @Override
    public void deployProcess(File processDescriptionFile) throws ProcessExecutorServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getHost() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RunningProcess> getRunningProcesses() throws ProcessExecutorServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void testServiceHost(String host) throws ProcessExecutorServiceException {
        // TODO Auto-generated method stub

    }

}
