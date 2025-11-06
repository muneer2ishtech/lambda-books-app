package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.dynamo.BookDao;
import fi.ishtech.practice.bookapp.lambda.utils.IdUtil;
import fi.ishtech.practice.bookapp.lambda.utils.PayloadUtil;

/**
 * Handler for creating new book
 *
 * @author Muneer Ahmed Syed
 */
public class CreateBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(CreateBookHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		String input = request.getBody();
		log.debug("Input: {}", input);

		// TODO: assert book is not null
		// TODO: assert book.id is null

		Map<String, String> responseBody = new HashMap<>();

		try {
			BookDto book = MAPPER.readValue(input, BookDto.class);
			book.setId(IdUtil.newId());

			BookDao.createBook(book);

			responseBody.put(BookDto.ID, book.getId());

			return PayloadUtil.successResponse(201, MAPPER.writeValueAsString(book));
		} catch (IllegalArgumentException e) {
			return PayloadUtil.badRequestResponse(e);
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

}