package fi.ishtech.practice.bookapp.lambda;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDbClientProvider {
    private static final DynamoDbClient CLIENT = DynamoDbClient.builder()
            .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "eu-west-1")))
            .build();

    public static DynamoDbClient getClient() {
        return CLIENT;
    }
}
