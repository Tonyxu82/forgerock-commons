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
urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-equal
This function SHALL take two arguments of data-type
"http://www.w3.org/2001/XMLSchema#dayTimeDuration”
and SHALL return an "http://www.w3.org/2001/XMLSchema#boolean".
This function shall perform its evaluation
according to the "op:duration-equal" function [XF] Section 10.4.5.
Note that the lexical representation of each argument
MUST be converted to a value expressed in fractional seconds [XF] Section 10.3.2.
*/

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

/**
 * urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-equal
 */
public class DaytimedurationEqual extends XACMLFunction {

    public DaytimedurationEqual()  {
    }
    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {
        FunctionArgument retVal = FunctionArgument.falseObject;
        if ( getArgCount() != 2) {
            return retVal;
        }

        Long duration1 = getArg(0).asDayTimeDuration(pip);
        Long duration2 = getArg(1).asDayTimeDuration(pip);

        if (duration1.longValue() == duration2.longValue()) {
            retVal = FunctionArgument.trueObject;
        }
        return retVal;
    }
}
