package fi.ishtech.practice.bookapp.lambda.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Mapper for BookDto to / from DynamoDb results
 *
 * @author Muneer Ahmed Syed
 */
public class BookMapper {

	public static Map<String, AttributeValue> makeAttributeMap(BookDto book) {
		Map<String, AttributeValue> item = new HashMap<>();

		if (StringUtils.isNotBlank(book.getId())) {
			item.put(BookDto.ID, DynamoDbUtil.buildStringAttribute(book.getId()));
		}

		if (StringUtils.isNotBlank(book.getTitle())) {
			item.put(BookDto.TITLE, DynamoDbUtil.buildStringAttribute(book.getTitle()));
		}

		if (StringUtils.isNotBlank(book.getAuthor())) {
			item.put(BookDto.AUTHOR, DynamoDbUtil.buildStringAttribute(book.getAuthor()));
		}

		if (book.getYear() != null) {
			item.put(BookDto.YEAR, DynamoDbUtil.buildNumberAttribute(book.getYear()));
		}

		if (book.getPrice() != null) {
			item.put(BookDto.PRICE, DynamoDbUtil.buildNumberAttribute(book.getPrice()));
		}

		return item;
	}

	public static BookDto fromAttributeMap(Map<String, AttributeValue> item) {
		if (item == null) {
			return null;
		}

		BookDto bookDto = new BookDto();
		bookDto.setId(DynamoDbUtil.getStringAttributeValue(item, BookDto.ID));
		bookDto.setTitle(DynamoDbUtil.getStringAttributeValue(item, BookDto.TITLE));
		bookDto.setAuthor(DynamoDbUtil.getStringAttributeValue(item, BookDto.AUTHOR));
		bookDto.setYear(DynamoDbUtil.getShortAttributeValue(item, BookDto.YEAR));
		bookDto.setPrice(DynamoDbUtil.getBigDecimalAttributeValue(item, BookDto.PRICE));

		return bookDto;
	}

	public static BookDto fromItemResponse(GetItemResponse resp) {
		if (resp == null || !resp.hasItem()) {
			return null;
		}

		return fromAttributeMap(resp.item());
	}

	public static List<BookDto> fromScanResponse(ScanResponse resp) {
		if (resp == null) {
			return null;
		}

		List<BookDto> books = new ArrayList<>();

		if (resp.hasItems()) {
			for (Map<String, AttributeValue> item : resp.items()) {
				books.add(fromAttributeMap(item));
			}
		}

		return books;
	}

}