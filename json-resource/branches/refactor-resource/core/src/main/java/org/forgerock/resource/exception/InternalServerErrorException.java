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

// JSON Resource
import org.forgerock.resource.exception.ResourceException;

/**
 * An exception that is thrown during an operation on a resource when the server
 * encountered an unexpected condition which prevented it from fulfilling the request.
 *
 * @author Paul C. Bryan
 */
public class InternalServerErrorException extends ResourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public InternalServerErrorException() {
        super(ResourceException.INTERNAL_ERROR);
    }
    
    /**
     * Constructs a new exception with the specified detail message.
     */
    public InternalServerErrorException(String message) {
        super(ResourceException.INTERNAL_ERROR, message);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     */
    public InternalServerErrorException(Throwable cause) {
        super(ResourceException.INTERNAL_ERROR, cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(ResourceException.INTERNAL_ERROR, message, cause);
    }
}
