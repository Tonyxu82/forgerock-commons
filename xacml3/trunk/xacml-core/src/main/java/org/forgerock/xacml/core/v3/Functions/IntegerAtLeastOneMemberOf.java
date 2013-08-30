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
 * urn:oasis:names:tc:xacml:x.x:function:type-at-least-one-member-of
 This function SHALL take two arguments that are both a bag of ‘type’ values.
 It SHALL return a “http://www.w3.org/2001/XMLSchema#boolean”.  The function SHALL evaluate to "True" if and
 only if at least one element of the first argument is contained in the second argument as determined by
 "urn:oasis:names:tc:xacml:x.x:function:type-is-in".
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

public class IntegerAtLeastOneMemberOf extends XACMLFunction {

    public IntegerAtLeastOneMemberOf()  {
    }
    public FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException {
        FunctionArgument retVal =  FunctionArgument.falseObject;
        int args = getArgCount();
        if (args != 2) {
            throw new IndeterminateException("Function Requires 2 arguments, " +
                    "however " + args + " in stack.");
        }
        // Iterate Over the 2 DataBag's in Stack, Evaluate and determine if the Contents of a Another Bag contains
        // At least one Member of the First.
        try {
            DataBag firstBag = (DataBag) getArg(0).evaluate(pip);
            DataBag secondBag = (DataBag) getArg(1).evaluate(pip);

            // Verify our Data Type with First Data Bag's Data Type.
            if (firstBag.getType().getIndex() != secondBag.getType().getIndex()) {
                throw new IndeterminateException("First Bag Type: " + firstBag.getType().getTypeName() +
                        ", however the subsequent Bag Type was " + secondBag.getType()
                        .getTypeName());
            }
            // Iterate over the First Bag.
            boolean breakIt = false;
            for (int b = 0; b < firstBag.size(); b++) {
                DataValue dataValue1 = (DataValue) firstBag.get(b).evaluate(pip);
                for (int z = 0; z < secondBag.size(); z++) {
                    DataValue dataValue2 = (DataValue) secondBag.get(z).evaluate(pip);
                    // Check Equality by using this Types Equality Function.
                    IntegerEqual fEquals = new IntegerEqual();
                    fEquals.addArgument(dataValue2);
                    fEquals.addArgument(dataValue1);
                    FunctionArgument result = fEquals.evaluate(pip);
                    if (result.isTrue()) {
                        retVal = FunctionArgument.trueObject;
                        breakIt = true;
                        break;
                    }
                } // End of Inner Loop.
                if (breakIt) {
                    break;
                }
            } // End of Outer For Loop.
        } catch (Exception e) {
            throw new IndeterminateException("Iterating over Arguments Exception: " + e.getMessage());
        }
        // Return our Boolean Indicator for the At Least One Member Of.
        return retVal;
    }
}
