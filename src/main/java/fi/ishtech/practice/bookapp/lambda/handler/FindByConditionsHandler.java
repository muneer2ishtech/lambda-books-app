package fi.ishtech.practice.bookapp.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

<<<<<<< HEAD:src/main/java/fi/ishtech/practice/bookapp/lambda/FindByConditionsHandler.java
=======
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

/**
 * Handler for finding book by conditions
 *
 * @author Muneer Ahmed Syed
 */
>>>>>>> 22f352e (refactor package name):src/main/java/fi/ishtech/practice/bookapp/lambda/handler/FindByConditionsHandler.java
public class FindByConditionsHandler implements RequestHandler<BookDto, List<BookDto>> {

    private final DynamoDbClient dynamoDb = DynamoDbClientProvider.getClient();
    private final String table = System.getenv().getOrDefault("BOOK_TABLE", "books");

    @Override
    public List<BookDto> handleRequest(BookDto filter, Context context) {
        // Simple implementation: scan + filter in-code for title/author/year range
        ScanResponse resp = dynamoDb.scan(ScanRequest.builder().tableName(table).build());
        List<BookDto> list = new ArrayList<>();
        for (Map<String, AttributeValue> item : resp.items()) {
            boolean ok = true;
            if (filter.getTitle() != null) {
                ok = item.containsKey("title") && item.get("title").s().equals(filter.getTitle());
            }
            if (ok && filter.getAuthor() != null) {
                ok = item.containsKey("author") && item.get("author").s().equals(filter.getAuthor());
            }
            if (ok && filter.getYear() != null) {
                ok = item.containsKey("year") && Integer.valueOf(item.get("year").n()).equals(filter.getYear());
            }
            if (ok) {
                BookDto b = new BookDto();
                if (item.containsKey("id")) b.setId(item.get("id").s());
                if (item.containsKey("title")) b.setTitle(item.get("title").s());
                if (item.containsKey("author")) b.setAuthor(item.get("author").s());
                if (item.containsKey("year")) b.setYear(Integer.valueOf(item.get("year").n()));
                if (item.containsKey("price")) b.setPrice(Double.valueOf(item.get("price").n()));
                list.add(b);
            }
        }
        return list;
    }
}
