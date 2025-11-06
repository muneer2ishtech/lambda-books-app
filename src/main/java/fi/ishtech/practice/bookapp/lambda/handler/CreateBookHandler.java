package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import fi.ishtech.practice.bookapp.lambda.utils.IdUtil;
import fi.ishtech.practice.bookapp.lambda.utils.PayloadUtil;
import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * Handler for creating new book
 *
 * @author Muneer Ahmed Syed
 */
public class CreateBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(CreateBookHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		String input = request.getBody();
		log.debug("Input: {}", input);

		Map<String, String> responseBody = new HashMap<>();

		BookDto book;
		try {
			book = MAPPER.readValue(input, BookDto.class);
			createBook(book);

			responseBody.put(BookDto.ID, book.getId());

			// @formatter:off
			return PayloadUtil.successResponse(201, MAPPER.writeValueAsString(book));
			// @formatter:on
		} catch (IllegalArgumentException e) {
			return PayloadUtil.errorResponse(400, "Bad Request", e);
		} catch (Exception e) {
			return PayloadUtil.errorResponse(500, "Internal server error", e);
		}
	}

	private String createBook(@NotNull BookDto book) {
		log.debug("Input Book:{}", book);

		// TODO: assert book is not null

		// TODO: assert book.id is null
		book.setId(IdUtil.newId());

		Map<String, AttributeValue> item = BookMapper.makeAttributeMap(book);

		dynamoDb.putItem(PutItemRequest.builder().tableName(AppConstants.TABLE_BOOK).item(item).build());
		log.debug("Output Book ID:{}", book.getId());

		return book.getId();
	}

}