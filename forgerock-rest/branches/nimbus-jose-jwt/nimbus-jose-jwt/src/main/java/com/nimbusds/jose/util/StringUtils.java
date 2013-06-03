package com.nimbusds.jose.util;


import java.nio.charset.Charset;

import com.nimbusds.jose.JOSEException;


/**
 * String utilities.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-16)
 */
public class StringUtils {


	/**
	 * Converts the specified string to a byte array.
	 *
	 * @param s The input string to convert. Must be UTF-8 encoded and not
	 *          {@code null}.
	 *
	 * @return The resulting byte array.
	 */
	public static byte[] toByteArray(final String s) {

		return s.getBytes(Charset.forName("UTF-8"));
	}


	/**
	 * Prevents public instantiation.
	 */
	private StringUtils() {

	}
}