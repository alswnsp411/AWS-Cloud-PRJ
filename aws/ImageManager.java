package aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;

public class ImageManager {

    private final AmazonEC2 ec2;

    public ImageManager(AmazonEC2 ec2Client) {
        this.ec2 = ec2Client;
    }

    public void listImages() {
        System.out.println("Listing images....");

        DescribeImagesRequest request = new DescribeImagesRequest();
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

        request.getFilters().add(new Filter().withName("name").withValues("htcondor-slave-image"));
        request.setRequestCredentialsProvider(credentialsProvider);

        DescribeImagesResult results = ec2.describeImages(request);

        for (Image images : results.getImages()) {
            System.out.printf("[ImageID] %s, [Name] %s, [Owner] %s\n",
                    images.getImageId(), images.getName(), images.getOwnerId());
        }

    }
}
