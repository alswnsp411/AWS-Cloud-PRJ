package aws;

/*
 * Cloud Computing
 *
 * Dynamic Resource Management Tool
 * using AWS Java SDK Library
 *
 */

import com.amazonaws.services.ec2.AmazonEC2;
import java.util.Scanner;

public class awsTest {

    public static void main(String[] args) {

        final AmazonEC2 ec2 = AWSClientUtil.getEC2Client();
        final InstanceManager instanceManager = new InstanceManager(ec2);
        final ZoneManager zoneManager = new ZoneManager(ec2);
        final RegionManager regionManager = new RegionManager(ec2);
        final ImageManager imageManager = new ImageManager(ec2);
        final CMDManager cmdManager = new CMDManager();
        final HTCondorManager htCondorManager = new HTCondorManager(instanceManager, cmdManager);
        final MonitoringManager monitoringManager = new MonitoringManager();

        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;

        while (true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones        ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("  9. condor_status                10. instance CPU Utilization");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            if (menu.hasNextInt()) {
                number = menu.nextInt();
            } else {
                System.out.println("concentration!");
                break;
            }

            String instance_id = "";

            switch (number) {
                case 1:
                    instanceManager.listInstances();
                    break;

                case 2:
                    zoneManager.availableZones();
                    break;

                case 3:
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext()) {
                        instance_id = id_string.nextLine();
                    }

                    if (!instance_id.trim().isEmpty()) {
                        instanceManager.startInstance(instance_id);
                    }
                    break;

                case 4:
                    regionManager.availableRegions();
                    break;

                case 5:
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext()) {
                        instance_id = id_string.nextLine();
                    }

                    if (!instance_id.trim().isEmpty()) {
                        instanceManager.stopInstance(instance_id);
                    }
                    break;

                case 6:
                    System.out.print("Enter ami id: ");
                    String ami_id = "";
                    if (id_string.hasNext()) {
                        ami_id = id_string.nextLine();
                    }

                    if (!ami_id.trim().isEmpty()) {
                        instanceManager.createInstance(ami_id);
                    }
                    break;

                case 7:
                    System.out.print("Enter instance id: ");
                    if (id_string.hasNext()) {
                        instance_id = id_string.nextLine();
                    }

                    if (!instance_id.trim().isEmpty()) {
                        instanceManager.rebootInstance(instance_id);
                    }
                    break;

                case 8:
                    imageManager.listImages();
                    break;

                case 9:
                    htCondorManager.runCondorStatus();
                    break;

                case 10:
                    System.out.print("Enter EC2 instance id: ");
                    if (id_string.hasNext()) {
                        instance_id = id_string.nextLine();
                    }

                    if (!instance_id.trim().isEmpty()) {
                        monitoringManager.getEC2CPUUtilization(instance_id);
                    }
                    break;

                case 99:
                    System.out.println("bye!");
                    menu.close();
                    id_string.close();
                    return;

                default:
                    System.out.println("concentration!");
            }

        }
    }

}
	