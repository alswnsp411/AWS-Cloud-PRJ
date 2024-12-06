package aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class InstanceManager {

    private final AmazonEC2 ec2;

    public InstanceManager(AmazonEC2 ec2Client) {
        this.ec2 = ec2Client;
    }

    public void listInstances() {
        System.out.println("Listing instances....");
        boolean done = false;

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, [AMI] %s, [type] %s, [state] %10s, [monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }
    }

    public void startInstance(String instanceId) {
        System.out.printf("Starting .... %s\n", instanceId);
        DryRunSupportedRequest<StartInstancesRequest> dry_request =
                () -> {
                    StartInstancesRequest request = new StartInstancesRequest()
                            .withInstanceIds(instanceId);

                    return request.getDryRunRequest();
                };

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instanceId);

        ec2.startInstances(request);

        System.out.printf("Successfully started instance %s", instanceId);
    }

    public void stopInstance(String instanceId) {
        DryRunSupportedRequest<StopInstancesRequest> dry_request =
                () -> {
                    StopInstancesRequest request = new StopInstancesRequest()
                            .withInstanceIds(instanceId);

                    return request.getDryRunRequest();
                };

        try {
            StopInstancesRequest request = new StopInstancesRequest()
                    .withInstanceIds(instanceId);

            ec2.stopInstances(request);
            System.out.printf("Successfully stop instance %s\n", instanceId);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }


    public void createInstance(String ami_id) {

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

        System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                reservation_id, ami_id);

    }

    public void rebootInstance(String instance_id) {

        System.out.printf("Rebooting .... %s\n", instance_id);

        try {
            RebootInstancesRequest request = new RebootInstancesRequest()
                    .withInstanceIds(instance_id);

            RebootInstancesResult response = ec2.rebootInstances(request);

            System.out.printf(
                    "Successfully rebooted instance %s", instance_id);

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }

    }
}
