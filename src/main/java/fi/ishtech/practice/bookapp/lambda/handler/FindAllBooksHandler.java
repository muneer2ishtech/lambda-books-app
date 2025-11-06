package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.List;

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
import fi.ishtech.practice.bookapp.lambda.utils.PayloadUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.utils.CollectionUtils;

/**
 * Handler for finding all books
 *
 * @author Muneer Ahmed Syed
 */
public class FindAllBooksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(FindAllBooksHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		try {
			List<BookDto> books = findAllBooks();

			if (CollectionUtils.isNullOrEmpty(books)) {
				return PayloadUtil.notFoundResponse("Books not found");
			}

			return PayloadUtil.successResponse(MAPPER.writeValueAsString(books));
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

	private List<BookDto> findAllBooks() {
		// @formatter:off
		ScanResponse resp = dynamoDb.scan(
				ScanRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.build());
		// @formatter:on

		List<BookDto> books = BookMapper.fromScanResponse(resp);
		log.trace("Output Books:{}", books);

		return books;
	}

}