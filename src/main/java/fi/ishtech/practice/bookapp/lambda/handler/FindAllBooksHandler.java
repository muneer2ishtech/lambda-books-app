package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.dynamo.BookDao;
import fi.ishtech.practice.bookapp.lambda.utils.PayloadUtil;
import software.amazon.awssdk.utils.CollectionUtils;

/**
 * Handler for finding all books
 *
 * @author Muneer Ahmed Syed
 */
public class FindAllBooksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FindAllBooksHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		try {
			List<BookDto> books = BookDao.findAll();

			if (CollectionUtils.isNullOrEmpty(books)) {
				return PayloadUtil.notFoundResponse("Books not found");
			}

			return PayloadUtil.successResponse(MAPPER.writeValueAsString(books));
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

}