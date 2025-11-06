package fi.ishtech.practice.bookapp.lambda.utils;

import java.util.Map;
import java.util.UUID;

import fi.ishtech.practice.bookapp.lambda.AppConstants;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Util of ID related
 *
 * @author Muneer Ahmed Syed
 */
public class IdUtil {

	public static final Map<String, AttributeValue> makeKey(String id) {
		return Map.of(AppConstants.STR_ID, AttributeValue.builder().s(id).build());
	}

	public static final String newId() {
		return UUID.randomUUID().toString();
	}

}