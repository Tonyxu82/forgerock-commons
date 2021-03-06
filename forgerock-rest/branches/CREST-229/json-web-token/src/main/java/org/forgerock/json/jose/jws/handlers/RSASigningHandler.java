/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock AS.
 */

package org.forgerock.json.jose.jws.handlers;

import org.forgerock.json.jose.exceptions.JwsSigningException;
import org.forgerock.json.jose.exceptions.JwsVerifyingException;
import org.forgerock.json.jose.jws.JwsAlgorithm;
import org.forgerock.json.jose.utils.Utils;
import org.forgerock.util.Reject;
import org.forgerock.util.SignatureUtil;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

/**
 * An implementation of the SigningHandler which can sign and verify using algorithms from the RSA family.
 *
 * @author Phill Cunnington
 * @since 2.0.0
 */
public class RSASigningHandler implements SigningHandler {

    private final SignatureUtil signatureUtil;
    private final Key key;

    /**
     * Constructs a new RSASigningHandler, with a SignatureUtil instance to delegate the signing and verifying calls to.
     *
     * @param key The key used to sign and verify the signature.
     * @param signatureUtil An instance of the SignatureUtil.
     */
    public RSASigningHandler(Key key, SignatureUtil signatureUtil) {
        this.key = key;
        this.signatureUtil = signatureUtil;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] sign(JwsAlgorithm algorithm, String data) {
        try {
            Reject.ifFalse(key instanceof PrivateKey, "RSA requires private key for signing.");
            return signatureUtil.sign((PrivateKey) key, algorithm.getAlgorithm(), data);
        } catch (SignatureException e) {
            if (e.getCause().getClass().isAssignableFrom(NoSuchAlgorithmException.class)) {
                throw new JwsSigningException("Unsupported Signing Algorithm, " + algorithm.getAlgorithm(), e);
            }
            throw new JwsSigningException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify(JwsAlgorithm algorithm, byte[] data, byte[] signature) {
        try {
            Reject.ifFalse(key instanceof PublicKey, "RSA requires public key for signature verification.");
            return signatureUtil.verify((PublicKey) key, algorithm.getAlgorithm(),
                    new String(data, Utils.CHARSET), signature);
        } catch (SignatureException e) {
            if (e.getCause().getClass().isAssignableFrom(NoSuchAlgorithmException.class)) {
                throw new JwsVerifyingException("Unsupported Signing Algorithm, " + algorithm.getAlgorithm(), e);
            }
            throw new JwsVerifyingException(e);
        }
    }
}
