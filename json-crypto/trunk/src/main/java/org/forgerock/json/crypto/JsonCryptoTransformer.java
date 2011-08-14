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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.crypto;

// JSON Fluent library
import org.forgerock.json.fluent.JsonException;
import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.fluent.JsonTransformer;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class JsonCryptoTransformer implements JsonTransformer {

    /** TODO: Description. */
    private JsonDecryptor decryptor;

    /** TODO: Description. */
    private JsonEncryptor encryptor;

    /**
     * TODO: Description.
     *
     * @param decryptor TODO.
     * @throws NullPointerException if {@code decryptor} is {@code null}.
     */
    public JsonCryptoTransformer(JsonDecryptor decryptor) {
        if (decryptor == null) {
            throw new NullPointerException();
        }
        this.decryptor = decryptor;
    }

    /**
     * TODO: Description.
     *
     * @param encryptor TODO.
     * @throws NullPointerException if {@code encryptor} is {@code null}.
     */
    public JsonCryptoTransformer(JsonEncryptor encryptor) {
        if (encryptor == null) {
            throw new NullPointerException();
        }
        this.encryptor = encryptor;
    }

    @Override
    public void transform(JsonNode node) throws JsonException {
        try {
            if (encryptor != null) { // transformer performs encryption
                node.setValue(new JsonCrypto(encryptor.getType(), encryptor.encrypt(node)).toJsonNode().getValue());
            } else if (JsonCrypto.isJsonCrypto(node)) { // transformer performs decryption and node properties match
                JsonCrypto crypto = new JsonCrypto(node);
                if (crypto.getType().equals(decryptor.getType())) { // only attempt decryption if type matches
                    node.setValue(decryptor.decrypt(crypto.getValue()).getValue());
                }
            }
        } catch (JsonCryptoException jce) {
            throw new JsonException(jce);
        }
    }
}
