/**
 *
 ~ DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 ~
 ~ Copyright (c) 2011-2013 ForgeRock AS. All Rights Reserved
 ~
 ~ The contents of this file are subject to the terms
 ~ of the Common Development and Distribution License
 ~ (the License). You may not use this file except in
 ~ compliance with the License.
 ~
 ~ You can obtain a copy of the License at
 ~ http://forgerock.org/license/CDDLv1.0.html
 ~ See the License for the specific language governing
 ~ permission and limitations under the License.
 ~
 ~ When distributing Covered Code, include this CDDL
 ~ Header Notice in each file and include the License file
 ~ at http://forgerock.org/license/CDDLv1.0.html
 ~ If applicable, add the following below the CDDL Header,
 ~ with the fields enclosed by brackets [] replaced by
 ~ your own identifying information:
 ~ "Portions Copyrighted [year] [name of copyright owner]"
 *
 */

package org.forgerock.xacml.core.v3.model;


/*
   This class can be thrown by the evaluation system for any errors

*/

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;

public class IndeterminateException extends XACML3EntitlementException {

    public IndeterminateException() {
        super();
    }

    public IndeterminateException(String message) {
        super(message);
    }

    /**
     * Constructor with specified Exception Message and
     * URN.
     *
     * @param message
     * @param urn
     */
    public IndeterminateException(String message, String urn) {
        super(message, urn);
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IndeterminateException(String message, String urn, String codeLocationTag) {
        super(message, urn, codeLocationTag);
    }

}

