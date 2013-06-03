package com.nimbusds.jose.crypto;


import java.math.BigInteger;

//import net.jcip.annotations.ThreadSafe;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import com.nimbusds.jose.DefaultJWSHeaderFilter;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeaderFilter;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.ReadOnlyJWSHeader;
import com.nimbusds.jose.util.Base64URL;


/**
 * Elliptic Curve Digital Signature Algorithm (ECDSA) verifier of 
 * {@link com.nimbusds.jose.JWSObject JWS objects}.
 *
 * <p>Supports the following JSON Web Algorithms (JWAs):
 *
 * <ul>
 *     <li>{@link com.nimbusds.jose.JWSAlgorithm#ES256}
 *     <li>{@link com.nimbusds.jose.JWSAlgorithm#ES384}
 *     <li>{@link com.nimbusds.jose.JWSAlgorithm#ES512}
 * </ul>
 *
 * <p>Accepts all {@link com.nimbusds.jose.JWSHeader#getReservedParameterNames
 * reserved JWS header parameters}. Modify the {@link #getJWSHeaderFilter
 * header filter} properties to restrict the acceptable JWS algorithms and
 * header parameters, or to allow custom JWS header parameters.
 * 
 * @author Axel Nennker
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-03-27)
 */
//@ThreadSafe
public class ECDSAVerifier extends ECDSAProvider implements JWSVerifier {


	/**
	 * The JWS header filter.
	 */
	private final DefaultJWSHeaderFilter headerFilter;


	/**
	 * The 'x' EC coordinate.
	 */
	private final BigInteger x;


	/**
	 * The 'y' EC coordinate.
	 */
	private final BigInteger y;



	/**
	 * Creates a new Elliptic Curve Digital Signature Algorithm (ECDSA) 
	 * verifier.
	 *
	 * @param x The 'x' coordinate for the elliptic curve point. Must not 
	 *          be {@code null}.
	 * @param y The 'y' coordinate for the elliptic curve point. Must not 
	 *          be {@code null}.
	 */
	public ECDSAVerifier(final BigInteger x, final BigInteger y) {

		if (x == null) {

			throw new IllegalArgumentException("The \"x\" EC coordinate must not be null");
		}

		this.x = x;

		if (y == null) {

			throw new IllegalArgumentException("The \"y\" EC coordinate must not be null");
		}

		this.y = y;

		headerFilter = new DefaultJWSHeaderFilter(supportedAlgorithms());
	}


	/**
	 * Gets the 'x' coordinate for the elliptic curve point.
	 *
	 * @return The 'x' coordinate.
	 */
	public BigInteger getX() {

		return x;
	}


	/**
	 * Gets the 'y' coordinate for the elliptic curve point.
	 *
	 * @return The 'y' coordinate.
	 */
	public BigInteger getY() {

		return y;
	}


	@Override
	public JWSHeaderFilter getJWSHeaderFilter() {

		return headerFilter;
	}


	@Override
	public boolean verify(final ReadOnlyJWSHeader header, 
		              final byte[] signedContent, 
		              final Base64URL signature)
		throws JOSEException {

		ECDSAParameters initParams = getECDSAParameters(header.getAlgorithm());
		X9ECParameters x9ECParameters = initParams.getX9ECParameters();
		Digest digest = initParams.getDigest();

		byte[] signatureBytes = signature.decode();

		// Split signature into R and S parts
		int rsByteArrayLength = ECDSAProvider.getSignatureByteArrayLength(header.getAlgorithm());

		byte[] rBytes = new byte[rsByteArrayLength / 2];
		byte[] sBytes = new byte[rsByteArrayLength / 2];

		try {
			System.arraycopy(signatureBytes, 0, rBytes, 0, rBytes.length);
			System.arraycopy(signatureBytes, rBytes.length, sBytes, 0, sBytes.length);

		} catch (Exception e) {

			throw new JOSEException("Invalid ECDSA signature format: " + e.getMessage(), e);
		}

		BigInteger r = new BigInteger(1, rBytes);
		BigInteger s = new BigInteger(1, sBytes);


		ECCurve curve = x9ECParameters.getCurve();
		ECPoint qB = curve.createPoint(x, y, false);
		ECPoint q = new ECPoint.Fp(curve, qB.getX(), qB.getY());

		ECDomainParameters ecDomainParameters = new ECDomainParameters(
			curve, 
			x9ECParameters.getG(), 
			x9ECParameters.getN(), 
			x9ECParameters.getH(),
			x9ECParameters.getSeed());

		ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(
			q, ecDomainParameters);

		org.bouncycastle.crypto.signers.ECDSASigner verifier = 
			new org.bouncycastle.crypto.signers.ECDSASigner();

		verifier.init(false, ecPublicKeyParameters);

		digest.update(signedContent, 0, signedContent.length);
		byte[] out = new byte[digest.getDigestSize()];
		digest.doFinal(out, 0);

		return verifier.verifySignature(out, r, s);
	}
}
