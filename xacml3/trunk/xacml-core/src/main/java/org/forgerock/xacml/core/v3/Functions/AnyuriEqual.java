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

/*
urn:oasis:names:tc:xacml:1.0:function:anyURI-equal
This function SHALL take two arguments of data-type
“http://www.w3.org/2001/XMLSchema#anyURI”
and SHALL return an  “http://www.w3.org/2001/XMLSchema#boolean”.
The function SHALL return “True”
if and only if the values of the two arguments are equal on a codepoint-by-codepoint basis.
*/

import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.XACMLFunction;

/**
 * urn:oasis:names:tc:xacml:1.0:function:anyURI-equal
 */
public class AnyuriEqual extends XACMLFunction {

    public AnyuriEqual()  {
    }

    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {
        FunctionArgument retVal =  FunctionArgument.falseObject;

        if ( getArgCount() != 2) {
            return retVal;
        }
        if ( (getArg(0).asAnyURI(pip)==null) || (getArg(1).asAnyURI(pip)==null ) )  {
            return retVal;
        }
        String s = getArg(0).asAnyURI(pip);
        if ( s.equals(getArg(1).asAnyURI(pip)) ) {
            retVal = FunctionArgument.trueObject;
        }
        return retVal;
    }
}
