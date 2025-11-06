package fi.ishtech.practice.bookapp.lambda.utils;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Muneer Ahmed Syed
 */
public class PayloadUtil {

	private static final String STATUS = "status";
	private static final String ERROR = "error";
	private static final String EXCEPTION = "exception";
	private static final String MESSAGE = "message";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";

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
	
	public static APIGatewayProxyResponseEvent errorResponse(int status, String error, Exception ex) {
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put(STATUS, status);
		errorBody.put(ERROR, error);
		errorBody.put(EXCEPTION, ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName());
		errorBody.put(MESSAGE, ex.getMessage());

		try {
			// @formatter:off
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(status)
					.withBody(MAPPER.writeValueAsString(errorBody))
					.withHeaders(CONTENT_TYPE_APPLICATION_JSON);
			// @formatter:on
		} catch (Exception e) {
			// fallback if JSON serialization fails
			// @formatter:off
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(500)
					.withBody("{\"status\":500,\"error\":\"Serialization failed\"}")
					.withHeaders(CONTENT_TYPE_APPLICATION_JSON);
			// @formatter:on
		}
	}

}