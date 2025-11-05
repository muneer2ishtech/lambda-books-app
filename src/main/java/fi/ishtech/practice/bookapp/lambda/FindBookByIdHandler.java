package fi.ishtech.practice.bookapp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

public class FindBookByIdHandler implements RequestHandler<String, BookDto> {

    private final DynamoDbClient dynamoDb = DynamoDbClientProvider.getClient();
    private final String table = System.getenv().getOrDefault("BOOK_TABLE", "books");

    @Override
    public BookDto handleRequest(String id, Context context) {
        Map<String, AttributeValue> key = Map.of("id", AttributeValue.builder().s(id).build());
        GetItemResponse resp = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(table)
                .key(key)
                .build());

        if (!resp.hasItem()) return null;
        Map<String, AttributeValue> item = resp.item();
        BookDto b = new BookDto();
        b.setId(item.get("id").s());
        if (item.containsKey("title")) b.setTitle(item.get("title").s());
        if (item.containsKey("author")) b.setAuthor(item.get("author").s());
        if (item.containsKey("year")) b.setYear(Integer.valueOf(item.get("year").n()));
        if (item.containsKey("price")) b.setPrice(Double.valueOf(item.get("price").n()));
        return b;
    }
}
