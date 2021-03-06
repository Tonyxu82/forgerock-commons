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
 * Copyright 2013 ForgeRock Inc.
 */

package org.forgerock.json.jose.jwt;

public enum JwtHeaderKey {

    TYP,      //TODO can actually by media types to in the case of JWS
    ALG,
    CUSTOM;

    public String value() {
        return toString();
    }

    public static JwtHeaderKey getHeaderKey(String headerKey) {
        try {
            return JwtHeaderKey.valueOf(headerKey);
        } catch (IllegalArgumentException e) {
            return CUSTOM;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
