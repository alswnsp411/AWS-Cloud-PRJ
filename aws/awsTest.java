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
            printMenu();
            number = getMenu(menu);

            String instanceId = "";

            switch (number) {
                case 1:
                    instanceManager.listInstances();
                    break;

                case 2:
                    zoneManager.availableZones();
                    break;

                case 3:
                    instanceId = getInputString(id_string, "Enter instance id: ");

                    if (instanceId != null && !instanceId.isEmpty()) {
                        instanceManager.startInstance(instanceId);
                    }
                    break;

                case 4:
                    regionManager.availableRegions();
                    break;

                case 5:
                    instanceId = getInputString(id_string, "Enter instance id: ");

                    if (instanceId != null && !instanceId.isEmpty()) {
                        instanceManager.stopInstance(instanceId);
                    }
                    break;

                case 6:
                    String amiId = getInputString(id_string, "Enter ami id: ");

                    if (amiId != null && !amiId.isEmpty()) {
                        instanceManager.createInstance(amiId);
                    }
                    break;

                case 7:
                    instanceId = getInputString(id_string, "Enter instance id: ");

                    if (instanceId != null && !instanceId.isEmpty()) {
                        instanceManager.rebootInstance(instanceId);
                    }
                    break;

                case 8:
                    imageManager.listImages();
                    break;

                case 9:
                    htCondorManager.runCondorStatus();
                    break;

                case 10:
                    instanceId = getInputString(id_string, "Enter EC2 instance id: ");

                    if (instanceId != null && !instanceId.isEmpty()) {
                        monitoringManager.getMonitoring(instanceId);
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

    private static void printMenu() {
        System.out.println("                                                            ");
        System.out.println("                                                            ");
        System.out.println("------------------------------------------------------------");
        System.out.println("           Amazon AWS Control Panel using SDK               ");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1. list instance                2. available zones        ");
        System.out.println("  3. start instance               4. available regions      ");
        System.out.println("  5. stop instance                6. create instance        ");
        System.out.println("  7. reboot instance              8. list images            ");
        System.out.println("  9. condor_status                10. monitoring");
        System.out.println("                                  99. quit                   ");
        System.out.println("------------------------------------------------------------");
    }

    private static int getMenu(Scanner scanner) {
        System.out.print("Enter an integer: ");

        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        }
        scanner.nextLine();  //버퍼 비우기
        return -1;
    }

    private static String getInputString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (scanner.hasNext()) {
            return scanner.nextLine().trim();
        }
        return null;
    }

}
	