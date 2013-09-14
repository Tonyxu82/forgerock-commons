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
 * urn:oasis:names:tc:xacml:x.x:function:type-bag-size
 This function SHALL take a bag of ‘type’ values as an argument and SHALL return an
 “http://www.w3.org/2001/XMLSchema#integer” indicating the number of values in the bag.
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

/**
 * urn:oasis:names:tc:xacml:1.0:function:double-bag-size
 */
public class DoubleBagSize extends XACMLFunction {

    public DoubleBagSize()  {
    }
    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {

        // Only should have one Argument, a Bag of the applicable type.
        int args = getArgCount();
        if (args != 1) {
            throw new IndeterminateException("Function Requires 1 argument, " +
                    "however " + getArgCount() + " in stack.");
        }
        // Ensure Contents are of Applicable Type.
        FunctionArgument functionArgument = getArg(0);
        if (!functionArgument.getType().isType(DataType.Type.XACMLDOUBLETYPE)) {
            throw new IndeterminateException("Expecting a Double Type of Bag, but encountered a "+
                    functionArgument.getType().getTypeName());
        }
        // Ensure we have a DataBag.
        if (!(functionArgument instanceof DataBag))  {
            throw new IndeterminateException("Expecting a Bag, but encountered instead a "+
                    functionArgument.getType().getTypeName());
        }
        // return the Bag Size.
        return new DataValue(DataType.XACMLINTEGER, ((DataBag) functionArgument).size(), true);
    }
}
