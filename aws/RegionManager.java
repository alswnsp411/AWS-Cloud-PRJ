package aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;

public class RegionManager {

    private final AmazonEC2 ec2;

    public RegionManager(final AmazonEC2 ec2Client) {
        this.ec2 = ec2Client;
    }

    public void availableRegions() {

        System.out.println("Available regions ....");

        DescribeRegionsResult regions_response = ec2.describeRegions();

        for (Region region : regions_response.getRegions()) {
            System.out.printf(
                    "[region] %15s, [endpoint] %s\n",
                    region.getRegionName(),
                    region.getEndpoint());
        }
    }
}
