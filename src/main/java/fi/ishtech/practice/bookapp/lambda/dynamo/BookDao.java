package fi.ishtech.practice.bookapp.lambda.dynamo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Dao to read, write to books table
 *
 * @author Muneer Ahmed Syed
 */
public class BookDao {

	public static final Logger log = LoggerFactory.getLogger(BookDao.class);

	private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbUtil.getClient();

	public static List<BookDto> findAll() {
		// @formatter:off
		ScanResponse resp = DYNAMO_DB_CLIENT.scan(
				ScanRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.build());
		// @formatter:on

		List<BookDto> books = BookMapper.fromScanResponse(resp);
		log.trace("Output Books:{}", books);

		return books;
	}

	public static BookDto findOneById(String id) {
		log.debug("Input Book ID:{}", id);

		GetItemResponse resp = fetchOneById(id);

		BookDto book = BookMapper.fromItemResponse(resp);
		log.debug("Output Book:{}", book);

		return book;
	}

	public static boolean deleteByIdAndConfirm(String id) {
		deleteById(id);

		GetItemResponse ex = fetchOneById(id);
		boolean result = ex == null || !ex.hasItem();
		log.debug("Delete{} Book with id:{}", result ? "d successfully" : " failed for", id);

		return result;
	}

	public static BookDto createNew(BookDto book) {
		log.debug("Input Book:{}", book);

		Map<String, AttributeValue> item = BookMapper.makeAttributeMap(book);

		putItemInDb(item);
		// @formatter:on
		log.debug("Created Book with id:{}", book.getId());

		// TODO: return freshly fetched item from DB

		return book;
	}

	public static BookDto findAndUpdate(BookDto book) {
		log.debug("Input Book:{}", book);

		GetItemResponse ex = fetchOneById(book.getId());

		if (ex == null || !ex.hasItem()) {
			log.debug("Book with id:{} not found", book.getId());
			return null;
		}

		Map<String, AttributeValue> item = BookMapper.makeAttributeMap(book);

		putItemInDb(item);
		// @formatter:on
		log.debug("Updated Book of id:{}", book.getId());

		// TODO: return freshly fetched item from DB

		return book;
	}

	public static BookDto updateAttribsById(String id, Map<String, String> bookParams) {
		log.debug("Input id:{}, params:{}", id, bookParams);

		GetItemResponse ex = fetchOneById(id);

		if (ex == null || !ex.hasItem()) {
			log.debug("Book with id:{} not found", id);
			return null;
		}

		Map<String, AttributeValue> item = new HashMap<>(ex.item());

		String title = bookParams.get(BookDto.TITLE);
		if (StringUtils.isNotBlank(title)) {
			item.put(BookDto.TITLE, DynamoDbUtil.buildStringAttribute(title));
		}

		String author = bookParams.get(BookDto.AUTHOR);
		if (StringUtils.isNotBlank(author)) {
			item.put(BookDto.AUTHOR, DynamoDbUtil.buildStringAttribute(author));
		}

		String strYear = bookParams.get(BookDto.YEAR);
		if (StringUtils.isNotBlank(strYear)) {
			try {
				item.put(BookDto.YEAR, DynamoDbUtil.buildNumberAttribute(Short.valueOf(strYear)));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input for year:" + strYear, e);
			}
		}

		String strPrice = bookParams.get(BookDto.PRICE);
		if (StringUtils.isNotBlank(strPrice)) {
			try {
				item.put(BookDto.PRICE, DynamoDbUtil.buildNumberAttribute(new BigDecimal(strPrice)));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input for year:" + strYear, e);
			}
		}

		log.trace("Book item to update:{}", item);

		putItemInDb(item);
		log.debug("Updated Book attributes of id:{}", id);

		// TODO: return freshly fetched item from DB

		return BookMapper.fromAttributeMap(item);
	}

	private static GetItemResponse fetchOneById(String id) {
		Map<String, AttributeValue> key = IdUtil.makeKey(id);

		// @formatter:off
		GetItemResponse resp = DYNAMO_DB_CLIENT.getItem(
				GetItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.key(key)
					.build());
		// @formatter:on

		return resp;
	}

	private static DeleteItemResponse deleteById(String id) {
		log.debug("Input Book ID:{}", id);

		// @formatter:off
		return DYNAMO_DB_CLIENT.deleteItem(
				DeleteItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.key(IdUtil.makeKey(id))
					.build());
		// @formatter:on
	}

	private static PutItemResponse putItemInDb(Map<String, AttributeValue> item) {
		// @formatter:off
		return DYNAMO_DB_CLIENT.putItem(
				PutItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.item(item)
					.build());
		// @formatter:on
	}

}