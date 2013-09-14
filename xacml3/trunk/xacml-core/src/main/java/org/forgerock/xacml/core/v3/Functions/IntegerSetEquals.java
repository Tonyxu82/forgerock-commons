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
 * urn:oasis:names:tc:xacml:x.x:function:type-set-equals
 This function SHALL take two arguments that are both a bag of ‘type’ values.
 It SHALL return a “http://www.w3.org/2001/XMLSchema#boolean”.  It SHALL return the result of applying
 "urn:oasis:names:tc:xacml:1.0:function:and" to the application of "urn:oasis:names:tc:xacml:x.x:function:type-subset"
 to the first and second arguments and the application of "urn:oasis:names:tc:xacml:x.x:function:type-subset"
 to the second and first arguments.
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

public class IntegerSetEquals extends XACMLFunction {

    public IntegerSetEquals()  {
    }
    public FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException {

        int args = getArgCount();
        if (args != 2) {
            throw new IndeterminateException("Function Requires 2 arguments, " +
                    "however " + args + " in stack.");
        }
        // Iterate Over the 2 DataBag's in Stack, Evaluate and determine if First Bag is a Subset of the Second Bag and
        // in reverse and then And against each result.
        boolean firstSubSet = false;
        boolean secondSubSet = false;

        DataBag firstBag = null;
        DataBag secondBag = null;
        try {
            firstBag = (DataBag) getArg(0).doEvaluate(pip);
            secondBag = (DataBag) getArg(1).doEvaluate(pip);

            // Verify our Data Type with First Data Bag's Data Type.
            if (firstBag.getType().getIndex() != secondBag.getType().getIndex()) {
                throw new IndeterminateException("First Bag Type: " + firstBag.getType().getTypeName() +
                        ", however the subsequent Bag Type was " + secondBag.getType()
                        .getTypeName());
            }
            // Is the First Bag a SubSet of the Second?
            firstSubSet = this.SubSet(firstBag, secondBag, pip);
            // Is the Second Bag a SubSet of the First?
            secondSubSet = this.SubSet(secondBag, firstBag, pip);
        } catch (Exception e) {
            throw new IndeterminateException("Iterating over Arguments Exception: " + e.getMessage());
        }
        // And our Conditions.
        return (firstSubSet & secondSubSet) ? FunctionArgument.trueObject : FunctionArgument.falseObject;
    }

    /**
     * Perform a SubSet function against two defined Bags.
     * @param firstBag
     * @param secondBag
     * @param pip
     * @return
     * @throws XACML3EntitlementException
     */
    private boolean SubSet(DataBag firstBag, DataBag secondBag, XACMLEvalContext pip) throws
            XACML3EntitlementException {
        int subSetCount = 0;
        // Iterate over the First Bag.
        for (int b = 0; b < firstBag.size(); b++) {
            DataValue dataValue1 = (DataValue) firstBag.get(b).doEvaluate(pip);
            for (int z = 0; z<secondBag.size(); z++) {
                DataValue dataValue2 = (DataValue) secondBag.get(z).doEvaluate(pip);
                // Check Equality by using this Types Equality Function.
                IntegerEqual fEquals = new IntegerEqual();
                fEquals.addArgument(dataValue2);
                fEquals.addArgument(dataValue1);
                FunctionArgument result = fEquals.doEvaluate(pip);
                if (result.isTrue()) {
                    subSetCount++;
                    break;
                }
            } // End of Inner Loop.
        } // End of Outer For Loop.
        // Determine if we have in-fact a subSet.
        return (subSetCount == firstBag.size());
    }
}
