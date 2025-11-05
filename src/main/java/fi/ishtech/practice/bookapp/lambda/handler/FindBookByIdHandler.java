package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

/**
 * Handler for finding book by id
 *
 * @author Muneer Ahmed Syed
 */
public class FindBookByIdHandler implements RequestHandler<String, BookDto> {

	private static final Logger log = LoggerFactory.getLogger(FindBookByIdHandler.class);

	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public BookDto handleRequest(String id, Context context) {
		log.debug("Input Book ID:{}", id);

		Map<String, AttributeValue> key = Map.of("id", AttributeValue.builder().s(id).build());

		// @formatter:off
        GetItemResponse resp = dynamoDb.getItem(
        		GetItemRequest.builder()
        			.tableName(AppConstants.TABLE_BOOK)
        			.key(key)
        		.build());
        // @formatter:on

		BookDto book = BookMapper.fromItemResponse(resp);
		log.debug("Output Book:{}", book);

		return book;
	}

}