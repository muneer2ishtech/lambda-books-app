package fi.ishtech.practice.bookapp.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.mapper.BookMapper;
import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

/**
 * Handler for finding book by conditions
 *
 * @author Muneer Ahmed Syed
 */
public class FindByConditionsHandler implements RequestHandler<BookDto, List<BookDto>> {

	private final DynamoDbClient dynamoDb = DynamoDbUtil.getClient();

	@Override
	public List<BookDto> handleRequest(BookDto filter, Context context) {
		// Simple implementation: scan + filter in-code for title/author/year range
		ScanResponse resp = dynamoDb.scan(ScanRequest.builder().tableName(AppConstants.TABLE_BOOK).build());
		List<BookDto> list = new ArrayList<>();
		for (Map<String, AttributeValue> item : resp.items()) {
			boolean ok = true;
			if (filter.getTitle() != null) {
				ok = item.containsKey("title") && item.get("title").s().equals(filter.getTitle());
			}
			if (ok && filter.getAuthor() != null) {
				ok = item.containsKey("author") && item.get("author").s().equals(filter.getAuthor());
			}
			if (ok && filter.getYear() != null) {
				ok = item.containsKey("year") && Integer.valueOf(item.get("year").n()).equals(filter.getYear());
			}

			if (ok) {
				list.add(BookMapper.fromAttributeMap(item));
			}
		}

		return list;
	}

}