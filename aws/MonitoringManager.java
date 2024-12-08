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
        long offsetInMilliseconds = 24 * 60 * 60000; // 최근 하루 데이터
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - offsetInMilliseconds);

        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withPeriod(60)
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
            Collections.sort(datapoints, Comparator.comparing(Datapoint::getTimestamp));

            for (Datapoint datapoint : datapoints) {
                System.out.printf("Timestamp: %s, Average CPU Utilization: %.2f%%%n",
                        datapoint.getTimestamp(), datapoint.getAverage());
            }
        }
    }
}
