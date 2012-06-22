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

package org.forgerock.resource.exception;


/**
 * An exception that is thrown during a operation on a resource when the
 * requested operation is malformed.
 * 
 * 
 */
public class BadRequestException extends ResourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public BadRequestException() {
        super(ResourceException.BAD_REQUEST);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     */
    public BadRequestException(final String message) {
        super(ResourceException.BAD_REQUEST, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     * @param cause
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(ResourceException.BAD_REQUEST, message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause
     */
    public BadRequestException(final Throwable cause) {
        super(ResourceException.BAD_REQUEST, cause);
    }

}
