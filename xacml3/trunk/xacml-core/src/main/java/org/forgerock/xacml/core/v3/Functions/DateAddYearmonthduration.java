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
 * urn:oasis:names:tc:xacml:3.0:function:date-add-yearMonthDuration
 This function SHALL take two arguments, the first SHALL be a “http://www.w3.org/2001/XMLSchema#date” and the second
 SHALL be a “http://www.w3.org/2001/XMLSchema#yearMonthDuration”.
 It SHALL return a result of “http://www.w3.org/2001/XMLSchema#date”.
 This function SHALL return the value by adding the second argument to the first argument according to
 the specification of adding duration to date [XS] Appendix E.
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.model.YearMonthDuration;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;
import org.joda.time.DateTime;

import java.util.Date;

/**
 *  urn:oasis:names:tc:xacml:3.0:function:date-add-yearMonthDuration
 */
public class DateAddYearmonthduration extends XACMLFunction {

    public DateAddYearmonthduration()  {
    }

    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {

        if ( getArgCount() != 2) {
            throw new XACML3EntitlementException("Function Requires 2 Arguments");
        }

        Date date = getArg(0).asDate(pip);
        YearMonthDuration duration = getArg(1).asYearMonthDuration(pip);
        DateTime dt = new DateTime(duration.add(date.getTime()));
        // Return Calculated DateTime Data Type.
        return new DataValue(DataType.XACMLDATE, dt.toDate(), true);
    }
}
