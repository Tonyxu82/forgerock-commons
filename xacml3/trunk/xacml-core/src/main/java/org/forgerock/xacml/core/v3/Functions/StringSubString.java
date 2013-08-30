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
package org.forgerock.xacml.core.v3.Functions;

/**
 *  urn:oasis:names:tc:xacml:3.0:function:string-substring
 This function SHALL take a first argument of data-type "http://www.w3.org/2001/XMLSchema#string" and a
 second and a third argument of type "http://www.w3.org/2001/XMLSchema#integer" and
 SHALL return a "http://www.w3.org/2001/XMLSchema#string".
 The result SHALL be the substring of the first argument beginning at the position given by the second argument
 and ending at the position before the position given by the third argument.
 The first character of the string has position zero.
 The negative integer value -1 given for the third arguments indicates the end of the string.
 If the second or third arguments are out of bounds, then the function MUST evaluate to Indeterminate with a
 status code of urn:oasis:names:tc:xacml:1.0:status:processing-error.
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

/**
 * urn:oasis:names:tc:xacml:3.0:function:string-substring
 */
public class StringSubString extends XACMLFunction {

    public StringSubString()  {
    }

    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {
        FunctionArgument retVal =  FunctionArgument.falseObject;
        if ( getArgCount() != 3) {
            return retVal;
        }
        // Obtain Argument Values
        String stringValue = getArg(0).asString(pip);
        Integer beginIndex = getArg(1).asInteger(pip);
        Integer endIndex = getArg(2).asInteger(pip);

        // Validate Arguments
        if (stringValue == null) {
            throw new XACML3EntitlementException("Syntax Error, No String Value", URN_SYNTAX_ERROR);
        }

        if (beginIndex == null) {
            throw new XACML3EntitlementException("Syntax Error, No Start Value", URN_SYNTAX_ERROR);
        } else if ( (beginIndex < 0) || (beginIndex > stringValue.length()) ) {
            throw new XACML3EntitlementException("Start Value is Out of Bounds Error", URN_PROCESSING_ERROR);
        }


        if (endIndex == null) {
            throw new XACML3EntitlementException("Syntax Error, No End Value", URN_SYNTAX_ERROR);
        } else if ( (endIndex < -1) || (endIndex > stringValue.length()) ) {
            throw new XACML3EntitlementException("End Value is Out of Bounds Error", URN_PROCESSING_ERROR);
        } else if (endIndex == -1) {
            endIndex = stringValue.length();
        }


        try {
           return new DataValue(DataType.XACMLSTRING,  stringValue.substring(beginIndex, endIndex), true);
        } catch(Exception e) {
            throw new XACML3EntitlementException(e.getMessage(), URN_PROCESSING_ERROR);
        }
    }
}
