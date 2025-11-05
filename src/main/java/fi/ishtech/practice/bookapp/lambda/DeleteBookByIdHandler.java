package fi.ishtech.practice.bookapp.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

/**
 * Handler for deleting book
 *
 * @author Muneer Ahmed Syed
 */
public class DeleteBookByIdHandler implements RequestHandler<String, String> {

	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public String handleRequest(String id, Context context) {
		// @formatter:off
		dynamoDb.deleteItem(DeleteItemRequest.builder()
				.tableName(AppConstants.TABLE_BOOK)
				.key(Map.of("id", AttributeValue.builder().s(id).build()))
				.build());
		// @formatter:on

		return id;
	}

}