package fi.ishtech.practice.bookapp.lambda.handler;

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
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Handler for finding book by id
 *
 * @author Muneer Ahmed Syed
 */
public class FindBookByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(FindBookByIdHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		Map<String, String> pathParams = request.getPathParameters();
		log.trace("Request PathParameters: {}", pathParams);

		String id = pathParams.get(BookDto.ID);
		log.debug("Input id:{}", id);

		if (StringUtils.isBlank(id)) {
			return PayloadUtil.badRequestResponse("Input id is mandatory");
		}

		try {
			BookDto book = findOneById(id);

			if (book == null) {
				return PayloadUtil.notFoundResponse("Book for id:" + id + " not found");
			}

			return PayloadUtil.successResponse(MAPPER.writeValueAsString(book));
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

	private BookDto findOneById(String id) {
		log.debug("Input Book ID:{}", id);

		Map<String, AttributeValue> key = IdUtil.makeKey(id);

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