package fi.ishtech.practice.bookapp.lambda.utils;

import java.math.BigDecimal;
import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue.Builder;

/**
 * Util for Dynamo DB
 *
 * @author Muneer Ahmed Syed
 */
public class DynamoDbUtil {

	// @formatter:off
	private static final DynamoDbClient DYNAMO_DB_CLIENT = 
			DynamoDbClient
				.builder()
				.region(Region.of(EnvUtil.getAwsRegionOrDefault()))
				.build();
	// @formatter:on

	public static DynamoDbClient getClient() {
		return DYNAMO_DB_CLIENT;
	}

	public static Builder attributeValueBuiler() {
		return AttributeValue.builder();
	}

	public static AttributeValue buildStringAttribute(String str) {
		return attributeValueBuiler().s(str).build();
	}

	public static AttributeValue buildNumberAttribute(String str) {
		return attributeValueBuiler().n(str).build();
	}

	public static AttributeValue buildNumberAttribute(Number num) {
		return buildNumberAttribute(num.toString());
	}

	private static AttributeValue getAttributeValue(Map<String, AttributeValue> item, String name) {
		return item.get(name);
	}

	public static String getStringAttributeValue(Map<String, AttributeValue> item, String name) {
		return getAttributeValue(item, name).s();
	}

	public static String getNumberAttributeValue(Map<String, AttributeValue> item, String name) {
		return getAttributeValue(item, name).n();
	}

	public static Short getShortAttributeValue(Map<String, AttributeValue> item, String name) {
		return Short.valueOf(getNumberAttributeValue(item, name));
	}

	public static BigDecimal getBigDecimalAttributeValue(Map<String, AttributeValue> item, String name) {
		return new BigDecimal(getNumberAttributeValue(item, name));
	}

}