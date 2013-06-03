package com.nimbusds.jose.jwk;


import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParameterList;

import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;


/**
 * The base abstract class for JSON Web Keys (JWKs). It serialises to a JSON
 * object.
 *
 * <p>The following JSON object members are common to all JWK types:
 *
 * <ul>
 *     <li>{@link #getKeyType kty} (required)
 *     <li>{@link #getKeyUse use} (optional)
 *     <li>{@link #getKeyID kid} (optional)
 * </ul>
 *
 * <p>Example JWK (of the Elliptic Curve type):
 *
 * <pre>
 * {
 *   "kty" : "EC",
 *   "crv" : "P-256",
 *   "x"   : "MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4",
 *   "y"   : "4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM",
 *   "use" : "enc",
 *   "kid" : "1"
 * }
 * </pre>
 *
 * @author Vladimir Dzhuvinov
 * @author Justin Richer
 * @version $version$ (2013-05-29)
 */
public abstract class JWK implements JSONAware {


	/**
	 * The MIME type of JWK objects: 
	 * {@code application/jwk+json; charset=UTF-8}
	 */
	public static final ContentType MIME_TYPE;


	static {

		final ParameterList params = new ParameterList();
		params.set("charset", "UTF-8");

		MIME_TYPE = new ContentType("application", "jwk+json", params);
	}


	/**
	 * The key type, required.
	 */
	private final KeyType kty;


	/**
	 * The key use, optional.
	 */
	private final Use use;


	/**
	 * The intended JOSE algorithm for the key, optional.
	 */
	private final Algorithm alg;


	/**
	 * The key ID, optional.
	 */
	private final String kid;


	/**
	 * X.509 certificate URL, optional.
	 */
	private final URL x5u;


	/**
	 * X.509 certificate thumbprint, optional.
	 */
	private final Base64URL x5t;


	/**
	 * The X.509 certificate chain, optional.
	 */
	private final List<Base64> x5c;


	/**
	 * Creates a new JSON Web Key (JWK).
	 *
	 * @param kty The key type. Must not be {@code null}.
	 * @param use The key use, {@code null} if not specified or if the key 
	 *            is intended for signing as well as encryption.
	 * @param alg The intended JOSE algorithm for the key, {@code null} if
	 *            not specified.
	 * @param kid The key ID, {@code null} if not specified.
	 * @param x5u The X.509 certificate URL, {@code null} if not specified.
	 * @param x5t The X.509 certificate thumbprint, {@code null} if not
	 *            specified.
	 * @param x5c The X.509 certificate chain, {@code null} if not 
	 *            specified.
	 */
	public JWK(final KeyType kty, final Use use, final Algorithm alg, final String kid,
		   final URL x5u, final Base64URL x5t, final List<Base64> x5c) {

		if (kty == null) {
			throw new IllegalArgumentException("The key type \"kty\" must not be null");
		}

		this.kty = kty;
		this.use = use;
		this.alg = alg;
		this.kid = kid;

		this.x5u = x5u;
		this.x5t = x5t;
		this.x5c = x5c;
	}


	/**
	 * Gets the type ({@code kty}) of this JWK.
	 *
	 * @return The key type.
	 */
	public KeyType getKeyType() {

		return kty;
	}


	/**
	 * Gets the use ({@code use}) of this JWK.
	 *
	 * @return The key use, {@code null} if not specified or if the key is
	 *         intended for signing as well as encryption.
	 */
	public Use getKeyUse() {

		return use;
	}


	/**
	 * Gets the intended JOSE algorithm ({@code alg}) for this JWK.
	 *
	 * @return The intended JOSE algorithm, {@code null} if not specified.
	 */
	public Algorithm getAlgorithm() {

		return alg;
	}


	/**
	 * Gets the ID ({@code kid}) of this JWK. The key ID can be used to 
	 * match a specific key. This can be used, for instance, to choose a 
	 * key within a {@link JWKSet} during key rollover. The key ID may also 
	 * correspond to a JWS/JWE {@code kid} header parameter value.
	 *
	 * @return The key ID, {@code null} if not specified.
	 */
	public String getKeyID() {

		return kid;
	}


	/**
	 * Gets the X.509 certificate URL ({@code x5u}) of this JWK.
	 *
	 * @return The X.509 certificate URL, {@code null} if not specified.
	 */
	public URL getX509CertURL() {

		return x5u;
	}


	/**
	 * Gets the X.509 certificate thumbprint ({@code x5t}) of this JWK.
	 *
	 * @return The X.509 certificate thumbprint, {@code null} if not
	 *         specified.
	 */
	public Base64URL getX509CertThumbprint() {

		return x5t;
	}


	/**
	 * Gets the X.509 certificate chain ({@code x5c}) of this JWK.
	 *
	 * @return The X.509 certificate chain as a unmodifiable list,
	 *         {@code null} if not specified.
	 */
	public List<Base64> getX509CertChain() {

		if (x5c == null) {
			return null;
		}

		return Collections.unmodifiableList(x5c);
	}


	/**
	 * Returns {@code true} if this JWK contains private or sensitive
	 * (non-public) parameters.
	 *
	 * @return {@code true} if this JWK contains private parameters, else
	 *         {@code false}.
	 */
	public abstract boolean isPrivate();


	/**
	 * Creates a copy of this JWK with all private or sensitive parameters 
	 * removed.
	 * 
	 * @return The newly created public JWK, or {@code null} if none can be
	 *         created.
	 */
	public abstract JWK toPublicJWK();


	/**
	 * Returns a JSON object representation of this JWK. This method is 
	 * intended to be called from extending classes.
	 *
	 * <p>Example:
	 *
	 * <pre>
	 * {
	 *   "kty" : "RSA",
	 *   "use" : "sig",
	 *   "kid" : "fd28e025-8d24-48bc-a51a-e2ffc8bc274b"
	 * }
	 * </pre>
	 *
	 * @return The JSON object representation.
	 */
	public JSONObject toJSONObject() {

		JSONObject o = new JSONObject();

		o.put("kty", kty.getValue());

		if (use != null) {

			if (use == Use.SIGNATURE) {
				o.put("use", "sig");
			}

			if (use == Use.ENCRYPTION) {
				o.put("use", "enc");
			}
		}

		if (alg != null) {
			o.put("alg", alg.getName());
		}

		if (kid != null) {
			o.put("kid", kid);
		}

		if (x5u != null) {
			o.put("x5u", x5u.toString());
		}

		if (x5t != null) {
			o.put("x5t", x5t.toString());
		}

		if (x5c != null) {
			o.put("x5c", x5c);
		}

		return o;
	}


	/**
	 * Returns the JSON object string representation of this JWK.
	 *
	 * @return The JSON object string representation.
	 */
	@Override
	public String toJSONString() {

		return toJSONObject().toString();
	}


	/**
	 * @see #toJSONString
	 */
	@Override
	public String toString() {

		return toJSONObject().toString();
	}


	/**
	 * Parses a JWK from the specified JSON object string representation. 
	 * The JWK must be an {@link ECKey}, an {@link RSAKey}, or a 
	 * {@link OctetSequenceKey}.
	 *
	 * @param s The JSON object string to parse. Must not be {@code null}.
	 *
	 * @return The JWK.
	 *
	 * @throws ParseException If the string couldn't be parsed to a
	 *                        supported JWK.
	 */
	public static JWK parse(final String s)
		throws ParseException {

		return parse(JSONObjectUtils.parseJSONObject(s));
	}


	/**
	 * Parses a JWK from the specified JSON object representation. The JWK 
	 * must be an {@link ECKey}, an {@link RSAKey}, or a 
	 * {@link OctetSequenceKey}.
	 *
	 * @param jsonObject The JSON object to parse. Must not be 
	 *                   {@code null}.
	 *
	 * @return The JWK.
	 *
	 * @throws ParseException If the JSON object couldn't be parsed to a 
	 *                        supported JWK.
	 */
	public static JWK parse(final JSONObject jsonObject)
		throws ParseException {

		KeyType kty = KeyType.parse(JSONObjectUtils.getString(jsonObject, "kty"));

		if (kty == KeyType.EC) {
			
			return ECKey.parse(jsonObject);

		} else if (kty == KeyType.RSA) {
			
			return RSAKey.parse(jsonObject);

		} else if (kty == KeyType.OCT) {
			
			return OctetSequenceKey.parse(jsonObject);

		} else {

			throw new ParseException("Unsupported key type \"kty\" parameter: " + kty, 0);
		}
	}
}
