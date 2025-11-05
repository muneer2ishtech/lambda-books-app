package fi.ishtech.practice.bookapp.lambda.utils;

import fi.ishtech.practice.bookapp.lambda.AppConstants;

/**
 * Util for Environment
 *
 * @author Muneer Ahmed Syed
 */
public class EnvUtil {

	public static final String getAwsRegionOrDefault() {
		return System.getenv().getOrDefault(AppConstants.STR_AWS_REGION, AppConstants.DEFAULT_AWS_REGION);
	}

}