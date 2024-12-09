package aws;

import static config.configConstants.HTCondorMainInstanceId;

import com.amazonaws.services.ec2.model.Instance;

public class HTCondorManager {

    private final InstanceManager instanceManager;
    private final CMDManager cmdManager;

    public HTCondorManager(final InstanceManager instanceManager, final CMDManager cmdManager) {
        this.instanceManager = instanceManager;
        this.cmdManager = cmdManager;
    }

    public void runCondorStatus() {
        Instance HTCondorMainInstance = instanceManager.getInstance(HTCondorMainInstanceId);
        boolean isInstanceRun = instanceManager.isInstanceRunning(HTCondorMainInstance);
        if (!isInstanceRun) {
            System.out.println("Check the main condor instance");
            return;
        }

        String HTCondorMainInstancePublicDNS = instanceManager.getInstancePublicDNS(HTCondorMainInstance);
        if (HTCondorMainInstancePublicDNS == null) {
            return;
        }

        System.out.println("connect to " + HTCondorMainInstancePublicDNS);
        cmdManager.runInstance(HTCondorMainInstancePublicDNS, "condor_status");
    }
}
