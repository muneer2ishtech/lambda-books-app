package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.Map;
import java.util.stream.Collectors;

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
import software.amazon.awssdk.utils.StringUtils;

/**
 * Handler for finding book by id
 *
 * @author Muneer Ahmed Syed
 */
public class UpdateBookAttribsByIdHandler
		implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger log = LoggerFactory.getLogger(UpdateBookAttribsByIdHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		Map<String, String> pathParams = request.getPathParameters();
		log.trace("Request PathParameters: {}", pathParams);

		String id = pathParams.get(BookDto.ID);
		log.debug("Input id:{}", id);

		if (StringUtils.isBlank(id)) {
			return PayloadUtil.badRequestResponse("Input id is mandatory");
		}

		Map<String, String> queryParams = request.getQueryStringParameters();
		log.trace("Request QueryStringParameters: {}", queryParams);

		if (CollectionUtils.isNullOrEmpty(queryParams)) {
			return PayloadUtil.badRequestResponse("No inputs to update");
		}

		// @formatter:off
		Map<String, String> bookParams = queryParams.entrySet().stream()
				.filter(entry -> BookDto.ATTRIB_NAMES.contains(entry.getKey()))
				.filter(entry -> StringUtils.isNotBlank(entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		// @formatter:on

		if (CollectionUtils.isNullOrEmpty(bookParams)) {
			return PayloadUtil.badRequestResponse("No relavant inputs to update");
		}

		try {
			BookDto output = BookDao.updateAttribsById(id, bookParams);

			if (output == null) {
				return PayloadUtil.notFoundResponse("Book for id:" + id + " not found");
			}

			return PayloadUtil.successResponse(MAPPER.writeValueAsString(output));
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

}