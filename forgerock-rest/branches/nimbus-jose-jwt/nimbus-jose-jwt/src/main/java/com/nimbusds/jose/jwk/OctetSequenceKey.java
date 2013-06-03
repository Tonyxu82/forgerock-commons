package com.nimbusds.jose.jwk;


import java.net.URL;
import java.util.List;
import java.text.ParseException;

//import net.jcip.annotations.Immutable;

import net.minidev.json.JSONObject;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;


/**
 * {@link KeyType#OCT Octet sequence} JSON Web Key (JWK), used to represent
 * symmetric keys. This class is immutable.
 *
 * <p>Example JSON object representation of an octet sequence JWK:
 *
 * <pre>
 * {
 *   "kty" : "oct",
 *   "alg" : "A128KW",
 *   "k"   : "GawgguFyGrWKav7AX4VKUg"
 * }
 * </pre>
 * 
 * @author Justin Richer
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-29)
 */
//@Immutable
public class OctetSequenceKey extends JWK {


	/**
	 * The symmetric key value.
	 */
	private final Base64URL k;

	/**
	 * Implements a builder pattern for constructing octet sequence JWKs.
	 *
	 * <p>Example use:
	 *
	 * <pre>
	 * OctetSequenceKey key = new OctetSequenceKey.Builder(k).
	 *                        setAlgorithm(JWSAlgorithm.HS512).
	 *                        setKeyID("123").
	 *                        build();
	 * </pre>
	 */
	public static class Builder {


		/**
		 * The symmetric key value.
		 */
		private final Base64URL k;


		/**
		 * The key use, optional.
		 */
		private Use use;


		/**
		 * The intended JOSE algorithm for the key, optional.
		 */
		private Algorithm alg;


		/**
		 * The key ID, optional.
		 */
		private String kid;


		/**
		 * X.509 certificate URL, optional.
		 */
		private URL x5u;


		/**
		 * X.509 certificate thumbprint, optional.
		 */
		private Base64URL x5t;


		/**
		 * The X.509 certificate chain, optional.
		 */
		private List<Base64> x5c;


		/**
		 * Creates a new octet sequence JWK builder.
		 *
		 * @param k The key value. It is represented as the Base64URL 
		 *          encoding of value's big endian representation. Must
		 *          not be {@code null}.
		 */
		public Builder(final Base64URL k) {

			if (k == null) {
				throw new IllegalArgumentException("The key value must not be null");
			}

			this.k = k;
		}


		/**
		 * Sets the use ({@code use}) of the JWK.
		 *
		 * @param use The key use, {@code null} if not specified or if 
		 *            the key is intended for signing as well as 
		 *            encryption.
		 *
		 * @return This builder.
		 */
		public Builder setKeyUse(final Use use) {

			this.use = use;
			return this;
		}


		/**
		 * Sets the intended JOSE algorithm ({@code alg}) for the JWK.
		 *
		 * @param alg The intended JOSE algorithm, {@code null} if not 
		 *            specified.
		 *
		 * @return This builder.
		 */
		public Builder setAlgorithm(final Algorithm alg) {

			this.alg = alg;
			return this;
		}

		/**
		 * Sets the ID ({@code kid}) of the JWK. The key ID can be used 
		 * to match a specific key. This can be used, for instance, to 
		 * choose a key within a {@link JWKSet} during key rollover. 
		 * The key ID may also correspond to a JWS/JWE {@code kid} 
		 * header parameter value.
		 *
		 * @param kid The key ID, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder setKeyID(final String kid) {

			this.kid = kid;
			return this;
		}


		/**
		 * Sets the X.509 certificate URL ({@code x5u}) of the JWK.
		 *
		 * @param x5u The X.509 certificate URL, {@code null} if not 
		 *            specified.
		 *
		 * @return This builder.
		 */
		public Builder setX509CertURL(final URL x5u) {

			this.x5u = x5u;
			return this;
		}


		/**
		 * Sets the X.509 certificate thumbprint ({@code x5t}) of the
		 * JWK.
		 *
		 * @param x5t The X.509 certificate thumbprint, {@code null} if 
		 *            not specified.
		 *
		 * @return This builder.
		 */
		public Builder setX509CertThumbprint(final Base64URL x5t) {

			this.x5t = x5t;
			return this;
		}

		/**
		 * Sets the X.509 certificate chain ({@code x5c}) of the JWK.
		 *
		 * @param x5c The X.509 certificate chain as a unmodifiable 
		 *            list, {@code null} if not specified.
		 *
		 * @return This builder.
		 */
		public Builder setX509CertChain(final List<Base64> x5c) {

			this.x5c = x5c;
			return this;
		}

		/**
		 * Builds a new octet sequence JWK.
		 *
		 * @return The octet sequence JWK.
		 */
		public OctetSequenceKey build() {

			return new OctetSequenceKey(k, use, alg, kid, x5u, x5t, x5c);
		}
	}

	
	/**
	 * Creates a new octet sequence JSON Web Key (JWK) with the specified
	 * parameters.
	 *
	 * @param k   The key value. It is represented as the Base64URL 
	 *            encoding of value's big endian representation. Must not 
	 *            be {@code null}.
	 * @param use The key use. {@code null} if not specified.
	 * @param alg The intended JOSE algorithm for the key, {@code null} if
	 *            not specified.
	 * @param kid The key ID. {@code null} if not specified.
	 * @param x5u The X.509 certificate URL, {@code null} if not specified.
	 * @param x5t The X.509 certificate thumbprint, {@code null} if not
	 *            specified.
	 * @param x5c The X.509 certificate chain, {@code null} if not 
	 *            specified.
	 */
	public OctetSequenceKey(final Base64URL k, final Use use, final Algorithm alg, final String kid,
		                final URL x5u, final Base64URL x5t, final List<Base64> x5c) {
	
		super(KeyType.OCT, use, alg, kid, x5u, x5t, x5c);

		if (k == null) {
			throw new IllegalArgumentException("The key value must not be null");
		}

		this.k = k;
	}
    

	/**
	 * Returns the value of this octet sequence key. 
	 *
	 * @return The key value. It is represented as the Base64URL encoding 
	 *         of the coordinate's big endian representation.
	 */
	public Base64URL getKeyValue() {

		return k;
	}
	
	
	/**
	 * Returns a copy of this octet sequence key value as a byte array.
	 * 
	 * @return The key value as a byte array.
	 */
	public byte[] toByteArray() {

		return getKeyValue().decode();
	}


	/**
	 * Octet sequence (symmetric) keys are never considered public, this 
	 * method always returns {@code true}.
	 *
	 * @return {@code true}
	 */
	@Override
	public boolean isPrivate() {

		return true;
	}


	/**
	 * Octet sequence (symmetric) keys are never considered public, this 
	 * method always returns {@code null}.
	 *
	 * @return {@code null}
	 */
	@Override
	public OctetSequenceKey toPublicJWK() {

		return null;
	}
	

	@Override
	public JSONObject toJSONObject() {

		JSONObject o = super.toJSONObject();

		// Append key value
		o.put("k", k.toString());
		
		return o;
	}


	/**
	 * Parses an octet sequence JWK from the specified JSON object string 
	 * representation.
	 *
	 * @param s The JSON object string to parse. Must not be {@code null}.
	 *
	 * @return The octet sequence JWK.
	 *
	 * @throws ParseException If the string couldn't be parsed to an octet
	 *                        sequence JWK.
	 */
	public static OctetSequenceKey parse(final String s)
		throws ParseException {

		return parse(JSONObjectUtils.parseJSONObject(s));
	}

	
	/**
	 * Parses an octet sequence JWK from the specified JSON object 
	 * representation.
	 *
	 * @param jsonObject The JSON object to parse. Must not be 
	 *                   @code null}.
	 *
	 * @return The octet sequence JWK.
	 *
	 * @throws ParseException If the JSON object couldn't be parsed to an
	 *                        octet sequence JWK.
	 */
	public static OctetSequenceKey parse(final JSONObject jsonObject) 
		throws ParseException {

		// Parse the mandatory parameters first
		Base64URL k = new Base64URL(JSONObjectUtils.getString(jsonObject, "k"));

		// Check key type
		KeyType kty = KeyType.parse(JSONObjectUtils.getString(jsonObject, "kty"));

		if (kty != KeyType.OCT) {

			throw new ParseException("The key type \"kty\" must be oct", 0);
		}
		
		// Get optional key use
		Use use = null;

		if (jsonObject.containsKey("use")) {
			use = Use.parse(JSONObjectUtils.getString(jsonObject, "use"));
		}

		// Get optional intended algorithm
		Algorithm alg = null;

		if (jsonObject.containsKey("alg")) {
			alg = new Algorithm(JSONObjectUtils.getString(jsonObject, "alg"));
		}

		// Get optional key ID
		String kid = null;

		if (jsonObject.containsKey("kid")) {
			kid = JSONObjectUtils.getString(jsonObject, "kid");
		}

		// Get optional X.509 cert URL
		URL x5u = null;

		if (jsonObject.containsKey("x5u")) {
			x5u = JSONObjectUtils.getURL(jsonObject, "x5u");	
		}

		// Get optional X.509 cert thumbprint
		Base64URL x5t = null;

		if (jsonObject.containsKey("x5t")) {
			x5t = new Base64URL(JSONObjectUtils.getString(jsonObject, "x5t"));
		}

		// Get optional X.509 cert chain
		List<Base64> x5c = null;

		if (jsonObject.containsKey("x5c")) {
			x5c = X509CertChainUtils.parseX509CertChain(JSONObjectUtils.getJSONArray(jsonObject, "x5c"));	
		}

		return new OctetSequenceKey(k, use, alg, kid, x5u, x5t, x5c);
	}
}
