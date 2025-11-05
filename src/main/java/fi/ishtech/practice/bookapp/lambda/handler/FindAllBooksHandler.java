package fi.ishtech.practice.bookapp.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
<<<<<<< HEAD:src/main/java/fi/ishtech/practice/bookapp/lambda/FindAllBooksHandler.java
=======

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
>>>>>>> 22f352e (refactor package name):src/main/java/fi/ishtech/practice/bookapp/lambda/handler/FindAllBooksHandler.java
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FindAllBooksHandler implements RequestHandler<Object, List<BookDto>> {

    private final DynamoDbClient dynamoDb = DynamoDbClientProvider.getClient();
    private final String table = System.getenv().getOrDefault("BOOK_TABLE", "books");

    @Override
    public List<BookDto> handleRequest(Object input, Context context) {
        ScanResponse resp = dynamoDb.scan(ScanRequest.builder().tableName(table).build());
        List<BookDto> list = new ArrayList<>();
        for (Map<String, AttributeValue> item : resp.items()) {
            BookDto b = new BookDto();
            if (item.containsKey("id")) b.setId(item.get("id").s());
            if (item.containsKey("title")) b.setTitle(item.get("title").s());
            if (item.containsKey("author")) b.setAuthor(item.get("author").s());
            if (item.containsKey("year")) b.setYear(item.containsKey("year") ? Integer.valueOf(item.get("year").n()) : null);
            if (item.containsKey("price")) b.setPrice(item.containsKey("price") ? Double.valueOf(item.get("price").n()) : null);
            list.add(b);
        }
        return list;
    }
}
