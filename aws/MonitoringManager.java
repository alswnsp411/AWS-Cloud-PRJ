package aws;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MonitoringManager {
    private final AmazonCloudWatch cloudWatch;

    public MonitoringManager() {
        this.cloudWatch = AmazonCloudWatchClientBuilder.defaultClient();
    }

    public void getMonitoring(String instanceId) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMonitoringMenu();
            GetMetricStatisticsRequest request = getMenuRequest(scanner, instanceId);
            if (request == null) {
                break;
            }
            printMonitoringResult(instanceId, request);
        }

//        scanner.close();
    }

    private static void printMonitoringMenu() {
        System.out.println("                                                            ");
        System.out.println("                                                            ");
        System.out.println("------------------------------------------------------------");
        System.out.println("                        Monitoring Menu                     ");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1. CPU utilization                2. Network in        ");
        System.out.println("  3. Network out                    4. Network packets in");
        System.out.println("  5. Network packets out            6. Metadata no token");
        System.out.println("  7. CPU credit usage               8. CPU credit balance");
        System.out.println("  99. quit                   ");
        System.out.println("------------------------------------------------------------");
    }

    private GetMetricStatisticsRequest getMenuRequest(Scanner scanner, String instanceId) {
        long offsetInMilliseconds = 3 * 24 * 60 * 60000; // 최근 3일 데이터
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - offsetInMilliseconds);

        int menu = 0;

        while (true) {
            System.out.print("Enter an integer: ");

            if (scanner.hasNextInt()) {
                menu = scanner.nextInt();
            } else {
                System.out.println("Invalid input");
                scanner.nextLine();
            }

            if (menu == 1) {
                return getCustomRequest(instanceId, "CPUUtilization", startTime, endTime);
            }
            if (menu == 2) {
                return getCustomRequest(instanceId, "NetworkIn", startTime, endTime);
            }
            if (menu == 3) {
                return getCustomRequest(instanceId, "NetworkOut", startTime, endTime);
            }
            if (menu == 4) {
                return getCustomRequest(instanceId, "NetworkPacketsIn", startTime, endTime);
            }
            if (menu == 5) {
                return getCustomRequest(instanceId, "NetworkPacketsOut", startTime, endTime);
            }
            if (menu == 6) {
                return getCustomRequest(instanceId, "MetadataNoToken", startTime, endTime);
            }
            if (menu == 7) {
                return getCustomRequest(instanceId, "CPUCreditUsage", startTime, endTime);
            }
            if (menu == 8) {
                return getCustomRequest(instanceId, "CPUCreditBalance", startTime, endTime);
            }
            if (menu == 99) {
                return null;
            }
        }
    }

    private GetMetricStatisticsRequest getCustomRequest(String instanceId, String metricName,
                                                        Date startTime, Date endTime) {
        return new GetMetricStatisticsRequest()
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withPeriod(300)  //단위: 초
                .withNamespace("AWS/EC2")
                .withMetricName(metricName)
                .withStatistics("Average")
                .withDimensions(new com.amazonaws.services.cloudwatch.model.Dimension()
                        .withName("InstanceId")
                        .withValue(instanceId));
    }

    private void printMonitoringResult(String instanceId, GetMetricStatisticsRequest request) {
        GetMetricStatisticsResult response = cloudWatch.getMetricStatistics(request);
        List<Datapoint> datapoints = response.getDatapoints();

        if (datapoints.isEmpty()) {
            System.out.println("No metrics data found for instance: " + instanceId);
        } else {
            Collections.sort(datapoints, Comparator.comparing(Datapoint::getTimestamp).reversed());
            int limit = Math.min(datapoints.size(), 1440);  //데이터 포인트 개수 제한
            List<Datapoint> limitedDatapoints = datapoints.subList(0, limit);

            double maxAverage = limitedDatapoints.stream()
                    .mapToDouble(Datapoint::getAverage)
                    .max()
                    .orElse(0.0);

            System.out.println();
            System.out.println(response.getLabel()+" ("+response.getDatapoints().get(0).getUnit()+")");

            String currentDay = "";
            for (Datapoint datapoint : limitedDatapoints) {
                String fullTimestamp = datapoint.getTimestamp().toString();
                String datePart = fullTimestamp.substring(0, 10);
                String timePart = fullTimestamp.substring(11, 19);

                if (!currentDay.equals(datePart)) {
                    currentDay = datePart;
                    System.out.println();
                    System.out.printf("Timestamp: %s%n", currentDay);
                }

                double averageDatapoint = datapoint.getAverage();
                int barLength = (int) Math.round(averageDatapoint / maxAverage * 45);
                String bar = new String(new char[barLength]).replace("\0", "#");

                if(averageDatapoint>20){
                    System.out.printf("%s | %-45s %.0f%n", timePart, bar, averageDatapoint);
                }else{
                    System.out.printf("%s | %-45s %.2f%n", timePart, bar, averageDatapoint);
                }

            }
        }
    }

}
