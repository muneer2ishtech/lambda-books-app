package fi.ishtech.practice.bookapp.lambda.handler;

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
import software.amazon.awssdk.utils.StringUtils;

/**
 * Handler for updating an existing book
 *
 * @author Muneer Ahmed Syed
 */
public class UpdateBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(UpdateBookHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		String input = request.getBody();
		log.debug("Input: {}", input);

		if (input == null) {
			throw new IllegalArgumentException("Input body for Book is mandatory");
		}

		try {
			BookDto book = MAPPER.readValue(input, BookDto.class);			log.trace(input);

			if (StringUtils.isBlank(book.getId())) {
				throw new IllegalArgumentException("Input Book to update must have id");
			}

			BookDao.findAndUpdateBook(book);

			return PayloadUtil.successResponse(201, MAPPER.writeValueAsString(book));
		} catch (IllegalArgumentException e) {
			return PayloadUtil.badRequestResponse(e);
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

}