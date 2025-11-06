package fi.ishtech.practice.bookapp.lambda.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import fi.ishtech.practice.bookapp.lambda.dto.BookDto;
import fi.ishtech.practice.bookapp.lambda.dynamo.BookDao;
import fi.ishtech.practice.bookapp.lambda.utils.PayloadUtil;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Handler for deleting book
 *
 * @author Muneer Ahmed Syed
 */
public class DeleteBookByIdHandler
		implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	public static final Logger log = LoggerFactory.getLogger(DeleteBookByIdHandler.class);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		Map<String, String> pathParams = request.getPathParameters();
		log.trace("Request PathParameters: {}", pathParams);

		String id = pathParams.get(BookDto.ID);
		log.debug("Input id:{}", id);

		if (StringUtils.isBlank(id)) {
			return PayloadUtil.badRequestResponse("Input id is mandatory");
		}

		try {
			boolean result = BookDao.deleteByIdAndConfirm(id);
			if (result) {
				return PayloadUtil.goneResponse("Deleted successfully Book with id:" + id);
			} else {
				return PayloadUtil.internalServerErrorResponse("Delete failed for Book with id:" + id);
			}
		} catch (Exception e) {
			return PayloadUtil.internalServerErrorResponse(e);
		}
	}

}