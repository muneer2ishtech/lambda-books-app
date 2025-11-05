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
import fi.ishtech.practice.bookapp.lambda.utils.IdUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * Handler for creating new book
 *
 * @author Muneer Ahmed Syed
 */
public class CreateBookHandler implements RequestHandler<BookDto, String> {

	private static final Logger log = LoggerFactory.getLogger(CreateBookHandler.class);

	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public String handleRequest(BookDto book, Context context) {
		log.debug("Input Book:{}", book);

		// TODO: asert book is not null

		// TODO: assert book.id is null
		book.setId(IdUtil.newId());

		Map<String, AttributeValue> item = BookMapper.makeAttributeMap(book);

		dynamoDb.putItem(PutItemRequest.builder().tableName(AppConstants.TABLE_BOOK).item(item).build());
		log.debug("Output Book ID:{}", book.getId());

		return book.getId();
	}

}