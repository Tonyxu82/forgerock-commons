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
 * urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match
 This function decides a regular expression match.
 It SHALL take two arguments; the first is of type “http://www.w3.org/2001/XMLSchema#string”
 and the second is of type “urn:oasis:names:tc:xacml:1.0:data-type:x500Name”.
 It SHALL return an “http://www.w3.org/2001/XMLSchema#boolean”.

 The first argument SHALL be a regular expression and the second argument SHALL be an X.500 directory name.
 The function SHALL convert the second argument to type “http://www.w3.org/2001/XMLSchema#string”
 with urn:oasis:names:tc:xacml:3.0:function:string-from-x500Name,
 then apply “urn:oasis:names:tc:xacml:1.0:function:string-regexp-match”.
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * urn:oasis:names:tc:xacml:1.0:function:string-regexp-match
 */
public class X500NameRegexpMatch extends XACMLFunction {

    public X500NameRegexpMatch()  {
    }

    public FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException {

        FunctionArgument retVal = FunctionArgument.falseObject;
        // Validate argument List
        if (getArgCount() != 2) {
            return retVal;
        }
        // Check and Cast arguments.
        DataValue patternValue = (DataValue) getArg(0).doEvaluate(pip);
        DataValue dataValue = (DataValue) getArg(1).doEvaluate(pip);
        if ((patternValue == null) || (dataValue == null)) {
            throw new XACML3EntitlementException("No Pattern or Data Value Specified");
        }
        // Convert our URI to a simple String as per specification.
        StringFromX500Name stringFromX500Name= new StringFromX500Name();
        stringFromX500Name.addArgument(dataValue);
        dataValue = (DataValue) stringFromX500Name.doEvaluate(pip);
        // Apply the Pattern
        try {
            Pattern pattern = Pattern.compile(patternValue.asString(pip));
            Matcher matcher = pattern.matcher(dataValue.asString(pip));
            if (matcher.lookingAt()) {
                retVal = FunctionArgument.trueObject;
            }
        } catch (java.util.regex.PatternSyntaxException pse) {
            throw new XACML3EntitlementException("Pattern Syntax Exception: " + pse.getMessage());
        }
        return retVal;
    }

}
