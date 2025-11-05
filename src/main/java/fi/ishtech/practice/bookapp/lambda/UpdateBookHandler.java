package fi.ishtech.practice.bookapp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;

public class UpdateBookHandler implements RequestHandler<BookDto, String> {

    private final DynamoDbClient dynamoDb = DynamoDbClientProvider.getClient();
    private final String table = System.getenv().getOrDefault("BOOK_TABLE", "books");

    @Override
    public String handleRequest(BookDto book, Context context) {
        if (book.getId() == null) throw new IllegalArgumentException("id required");

        Map<String, String> exprNames = new HashMap<>();
        Map<String, AttributeValue> exprValues = new HashMap<>();
        StringBuilder setExpr = new StringBuilder();

        if (book.getTitle() != null) {
            append(setExpr, exprNames, exprValues, "#t", ":t", "title", AttributeValue.builder().s(book.getTitle()).build());
        }
        if (book.getAuthor() != null) {
            append(setExpr, exprNames, exprValues, "#a", ":a", "author", AttributeValue.builder().s(book.getAuthor()).build());
        }
        if (book.getYear() != null) {
            append(setExpr, exprNames, exprValues, "#y", ":y", "year", AttributeValue.builder().n(String.valueOf(book.getYear())).build());
        }
        if (book.getPrice() != null) {
            append(setExpr, exprNames, exprValues, "#p", ":p", "price", AttributeValue.builder().n(String.valueOf(book.getPrice())).build());
        }

        if (setExpr.length() == 0) return "nothing to update";

        UpdateItemRequest req = UpdateItemRequest.builder()
                .tableName(table)
                .key(Map.of("id", AttributeValue.builder().s(book.getId()).build()))
                .updateExpression("SET " + setExpr.toString())
                .expressionAttributeNames(exprNames)
                .expressionAttributeValues(exprValues)
                .build();

        dynamoDb.updateItem(req);
        return book.getId();
    }

    private void append(StringBuilder setExpr, Map<String,String> names, Map<String,AttributeValue> values,
                        String nameKey, String valueKey, String attrName, AttributeValue attrValue) {
        if (setExpr.length() > 0) setExpr.append(", ");
        setExpr.append(nameKey).append(" = ").append(valueKey);
        names.put(nameKey, attrName);
        values.put(valueKey, attrValue);
    }
}
