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
urn:oasis:names:tc:xacml:1.0:function:not
This function SHALL take one argument of data-type
“http://www.w3.org/2001/XMLSchema#boolean”. If the argument evaluates to "True",
then the result of the expression SHALL be "False". If the argument evaluates to "False",
then the result of the expression SHALL be "True".
*/

import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.model.IndeterminateException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.XACMLFunction;

/**
 * urn:oasis:names:tc:xacml:1.0:function:not
 */
public class Not extends XACMLFunction {

    public Not()  {
    }
    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {
        if ( getArgCount() != 1) {
            return FunctionArgument.falseObject;
        }
        Boolean argument = getArg(0).asBoolean(pip);
        if ((argument == null) ||!(argument instanceof Boolean)) {
            throw new IndeterminateException("Argument is null!");
        }
        if (argument.booleanValue() == true) {
            return  FunctionArgument.falseObject;
        }
        // Return Condition
        return FunctionArgument.trueObject;
    }
}
