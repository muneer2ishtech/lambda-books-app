package fi.ishtech.practice.bookapp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateBookHandler implements RequestHandler<BookDto, String> {

    private final DynamoDbClient dynamoDb = DynamoDbClientProvider.getClient();
    private final String table = System.getenv().getOrDefault("BOOK_TABLE", "books");

    @Override
    public String handleRequest(BookDto book, Context context) {
        if (book.getId() == null || book.getId().isEmpty()) {
            book.setId(UUID.randomUUID().toString());
        }

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(book.getId()).build());
        if (book.getTitle() != null) item.put("title", AttributeValue.builder().s(book.getTitle()).build());
        if (book.getAuthor() != null) item.put("author", AttributeValue.builder().s(book.getAuthor()).build());
        if (book.getYear() != null) item.put("year", AttributeValue.builder().n(String.valueOf(book.getYear())).build());
        if (book.getPrice() != null) item.put("price", AttributeValue.builder().n(String.valueOf(book.getPrice())).build());

        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(table)
                .item(item)
                .build());

        return book.getId();
    }
}
