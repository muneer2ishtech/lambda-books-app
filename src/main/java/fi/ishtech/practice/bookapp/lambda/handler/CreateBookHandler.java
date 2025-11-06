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
import fi.ishtech.practice.bookapp.lambda.utils.IdUtil;
import fi.ishtech.practice.bookapp.lambda.utils.PayloadUtil;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Handler for creating a new book
 *
 * @author Muneer Ahmed Syed
 */
public class CreateBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(CreateBookHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		String inputBody = request.getBody();
		log.trace("Input body: {}", inputBody);

		if (inputBody == null) {
			throw new IllegalArgumentException("Input body for Book is mandatory");
		}

		try {
			BookDto input = MAPPER.readValue(inputBody, BookDto.class);
			log.trace("Input:{}", input);

			if (StringUtils.isNotBlank(input.getId())) {
				throw new IllegalArgumentException("Input for new Book cannot have id");
			}

			input.setId(IdUtil.newId());

			BookDto output = BookDao.createNew(input);

			return PayloadUtil.successResponse(201, MAPPER.writeValueAsString(output));
		} catch (IllegalArgumentException e) {
			return PayloadUtil.badRequestResponse(e);
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

}
