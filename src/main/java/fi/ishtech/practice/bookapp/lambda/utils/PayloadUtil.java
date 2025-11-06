package fi.ishtech.practice.bookapp.lambda.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Muneer Ahmed Syed
 */
public class PayloadUtil {

	private static final Logger log = LoggerFactory.getLogger(PayloadUtil.class);

	private static final String STATUS = "status";
	private static final String ERROR = "error";
	private static final String EXCEPTION = "exception";
	private static final String MESSAGE = "message";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	private static final String SERIALIZATION_ERROR_MESSAGE = "{\"status\":500,\"error\":\"Serialization failed\"}";

	private static final Map<String, String> CONTENT_TYPE_APPLICATION_JSON = Map.of(CONTENT_TYPE, APPLICATION_JSON);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static APIGatewayProxyResponseEvent successResponse(int status, String body) {
		// @formatter:off
		return new APIGatewayProxyResponseEvent()
				.withStatusCode(status)
				.withBody(body)
				.withHeaders(CONTENT_TYPE_APPLICATION_JSON);
		// @formatter:on
	}

	public static APIGatewayProxyResponseEvent successResponse(String body) {
		return successResponse(200, body);
	}

	public static APIGatewayProxyResponseEvent errorResponse(int status, String error, Exception ex) {
		log.error("Exception:", ex);

		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put(STATUS, status);
		errorBody.put(ERROR, error);
		errorBody.put(EXCEPTION, ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName());
		errorBody.put(MESSAGE, ex.getMessage());

		return errorResponse(status, errorBody);
	}

	public static APIGatewayProxyResponseEvent errorResponse(int status, String error, String message) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put(STATUS, status);
		errorBody.put(ERROR, error);
		errorBody.put(MESSAGE, message);

		return errorResponse(status, errorBody);
	}

	private static APIGatewayProxyResponseEvent errorResponse(int status, Map<String, Object> errorBody) {
		try {
			// @formatter:off
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(status)
					.withBody(MAPPER.writeValueAsString(errorBody))
					.withHeaders(CONTENT_TYPE_APPLICATION_JSON);
			// @formatter:on
		} catch (Exception ex) {
			// fallback if JSON serialization fails
			log.warn("Exception in dealing with errorBody:", ex);

			// @formatter:off
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(500)
					.withBody(SERIALIZATION_ERROR_MESSAGE)
					.withHeaders(CONTENT_TYPE_APPLICATION_JSON);
			// @formatter:on
		}
	}

	public static APIGatewayProxyResponseEvent internalServerErrorResponse(Exception e) {
		return errorResponse(500, "Internal server error", e);
	}

	public static APIGatewayProxyResponseEvent internalServerErrorResponse(String message) {
		return errorResponse(500, "Internal server error", message);
	}

	public static APIGatewayProxyResponseEvent badRequestResponse(IllegalArgumentException e) {
		return errorResponse(400, "Bad Request", e);
	}

	public static APIGatewayProxyResponseEvent badRequestResponse(String message) {
		return errorResponse(400, "Bad Request", message);
	}

	public static APIGatewayProxyResponseEvent notFoundResponse(String message) {
		return errorResponse(404, "Not found", message);
	}

	public static APIGatewayProxyResponseEvent goneResponse(String message) {
		return errorResponse(410, "Gone", message);
	}

}