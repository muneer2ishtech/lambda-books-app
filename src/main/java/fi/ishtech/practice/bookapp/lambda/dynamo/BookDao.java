package fi.ishtech.practice.bookapp.lambda.dynamo;

import java.math.BigDecimal;
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
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.utils.StringUtils;

public class BookDao {

	public static final Logger log = LoggerFactory.getLogger(BookDao.class);

	private static final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	public static BookDto createBook(BookDto book) {
		log.debug("Input Book:{}", book);

		Map<String, AttributeValue> item = BookMapper.makeAttributeMap(book);

		// @formatter:off
		dynamoDb.putItem(
				PutItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.item(item)
					.build());
		// @formatter:on
		log.debug("Created Book with id:{}", book.getId());

		return book;
	}

	public static List<BookDto> findAllBooks() {
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
		dynamoDb.deleteItem(
				DeleteItemRequest.builder()
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

	public static BookDto findAndUpdateBook(BookDto book) {
		log.debug("Input Book:{}", book);
		
		GetItemResponse ex = fetchOneById(book.getId());
		
		if (ex == null || !ex.hasItem()) {
			log.debug("Book with id:{} not found", book.getId());
			return null;
		}

		Map<String, AttributeValue> item = BookMapper.makeAttributeMap(book);

		// @formatter:off
		dynamoDb.putItem(
				PutItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.item(item)
					.build());
		// @formatter:on
		log.debug("Updted Book with id:{}", book.getId());

		return book;
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

	public static BookDto updateAttribsById(String id, Map<String, String> bookParams) {
		log.debug("Input id:{}, params:{}", id, bookParams);

		GetItemResponse ex = fetchOneById(id);

		if (ex == null || !ex.hasItem()) {
			log.debug("Book with id:{} not found", id);
			return null;
		}

		Map<String, AttributeValue> item = ex.item();

		String title = bookParams.get(BookDto.TITLE);
		if (StringUtils.isEmpty(title)) {
			item.put(BookDto.TITLE, DynamoDbUtil.buildStringAttribute(title));
		}

		String author = bookParams.get(BookDto.AUTHOR);
		if (StringUtils.isEmpty(author)) {
			item.put(BookDto.AUTHOR, DynamoDbUtil.buildStringAttribute(author));
		}

		String strYear = bookParams.get(BookDto.YEAR);
		if (StringUtils.isEmpty(strYear)) {
			try {
				item.put(BookDto.YEAR, DynamoDbUtil.buildNumberAttribute(Short.valueOf(strYear)));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input for year:" + strYear, e);
			}
		}

		String strPrice = bookParams.get(BookDto.PRICE);
		if (StringUtils.isEmpty(strPrice)) {
			try {
				item.put(BookDto.PRICE, DynamoDbUtil.buildNumberAttribute(new BigDecimal(strPrice)));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input for year:" + strYear, e);
			}
		}

		putItemInDb(item);
		log.debug("Updated Book attributes of id:{}", id);

		// TODO: return freshly fetched item from DB

		return BookMapper.fromAttributeMap(item);
	}

	private static PutItemResponse putItemInDb(Map<String, AttributeValue> item) {
		// @formatter:off
		return dynamoDb.putItem(
				PutItemRequest.builder()
					.tableName(AppConstants.TABLE_BOOK)
					.item(item)
					.build());
		// @formatter:on
	}

}