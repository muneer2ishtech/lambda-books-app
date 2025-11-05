package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

/**
 * Handler for finding all books
 *
 * @author Muneer Ahmed Syed
 */
public class FindAllBooksHandler implements RequestHandler<Object, List<BookDto>> {

	private static final Logger log = LoggerFactory.getLogger(FindAllBooksHandler.class);

	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public List<BookDto> handleRequest(Object input, Context context) {
		ScanResponse resp = dynamoDb.scan(ScanRequest.builder().tableName(AppConstants.TABLE_BOOK).build());

		List<BookDto> list = BookMapper.fromScanResponse(resp);
		log.debug("Output Books:{}", list);

		return list;
	}
}
