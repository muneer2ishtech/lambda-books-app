package fi.ishtech.practice.bookapp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

public class DeleteBookByIdHandler implements RequestHandler<String, String> {

    private final DynamoDbClient dynamoDb = DynamoDbClientProvider.getClient();
    private final String table = System.getenv().getOrDefault("BOOK_TABLE", "books");

    @Override
    public String handleRequest(String id, Context context) {
        dynamoDb.deleteItem(DeleteItemRequest.builder()
                .tableName(table)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build());
        return id;
    }
}
