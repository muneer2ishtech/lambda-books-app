package fi.ishtech.practice.bookapp.lambda.dynamo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import fi.ishtech.practice.bookapp.lambda.utils.IdUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

public class BookDao {

	public static final Logger log = LoggerFactory.getLogger(BookDao.class);

	private static final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	public static BookDto findOneById(String id) {
		log.debug("Input Book ID:{}", id);

		GetItemResponse resp = fetchOneById(id);

		BookDto book = BookMapper.fromItemResponse(resp);
		log.debug("Output Book:{}", book);

		return book;
	}

	public static void deleteById(String id) {
		log.debug("Input Book ID:{}", id);

		// @formatter:off
		dynamoDb.deleteItem(DeleteItemRequest.builder()
				.tableName(AppConstants.TABLE_BOOK)
				.key(IdUtil.makeKey(id))
				.build());
		// @formatter:on
	}

	public static boolean deleteByIdAndConfirm(String id) {
		deleteById(id);

		GetItemResponse ex = fetchOneById(id);
		boolean result = ex == null || !ex.hasItem();
		log.debug("Delete{} Book with id:{}", result ? "d successfully" : " failed for", id);

		return result;
	}

	private static GetItemResponse fetchOneById(String id) {
		Map<String, AttributeValue> key = IdUtil.makeKey(id);

		// @formatter:off
		GetItemResponse resp = dynamoDb.getItem(
				GetItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.key(key)
					.build());
		// @formatter:on

		return resp;
	}

}