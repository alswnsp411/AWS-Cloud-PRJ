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

public class MonitoringManager {
    private final AmazonCloudWatch cloudWatch;

    public MonitoringManager() {
        this.cloudWatch = AmazonCloudWatchClientBuilder.defaultClient();
    }

    public void getEC2CPUUtilization(String instanceId) {
        long offsetInMilliseconds = 3 * 24 * 60 * 60000; // 최근 3일 데이터
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - offsetInMilliseconds);

        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withPeriod(300)  //단위: 초
                .withNamespace("AWS/EC2")
                .withMetricName("CPUUtilization")
                .withStatistics("Average")
                .withDimensions(new com.amazonaws.services.cloudwatch.model.Dimension()
                        .withName("InstanceId")
                        .withValue(instanceId));

        GetMetricStatisticsResult response = cloudWatch.getMetricStatistics(request);
        List<Datapoint> datapoints = response.getDatapoints();

        if (datapoints.isEmpty()) {
            System.out.println("No metrics data found for instance: " + instanceId);
        } else {
            Collections.sort(datapoints, Comparator.comparing(Datapoint::getTimestamp).reversed());
            int limit = Math.min(datapoints.size(), 1440);  //데이터 포인트 개수 제한
            List<Datapoint> limitedDatapoints = datapoints.subList(0, limit);

            System.out.println();
            System.out.println("Average CPU Utilization(%)");

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

                double cpuUtilization = datapoint.getAverage();
                int barLength = (int) Math.round(cpuUtilization / 2);
                String bar = new String(new char[barLength]).replace("\0", "#");


                System.out.printf("%s | %-50s %.2f%%%n", timePart, bar, cpuUtilization);
            }
        }
    }
}
