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
 * urn:oasis:names:tc:xacml:1.0:function:all-of-any
 This function applies a Boolean function between the elements of two bags.

 The expression SHALL be “True” if and only if the supplied predicate is “True” between each element of the
 first bag and any element of the second bag.

 This function SHALL take three arguments.

 The first argument SHALL be an <Function> element that names a
 Boolean function that takes two arguments of primitive types.

 The second argument SHALL be a bag of a primitive data-type.

 The third argument SHALL be a bag of a primitive data-type.

 The expression SHALL be evaluated as if the “urn:oasis:names:tc:xacml:3.0:function:any-of” function had been
 applied to each value of the first bag and the whole of the second bag using the supplied xacml:Function, and the
 results were then combined using “urn:oasis:names:tc:xacml:1.0:function:and”.

 For example, the following expression SHALL evaluate to "True":
 <Apply FunctionId=”urn:oasis:names:tc:xacml:1.0:function:all-of-any”>
 <Function FunctionId=”urn:oasis:names:tc:xacml:2.0:function:integer-greater-than”/>
 <Apply FunctionId=”urn:oasis:names:tc:xacml:1.0:function:integer-bag”>
 <AttributeValue DataType=”http://www.w3.org/2001/XMLSchema#integer”>10</AttributeValue>
 <AttributeValue DataType=”http://www.w3.org/2001/XMLSchema#integer”>20</AttributeValue>
 </Apply>
 <Apply FunctionId=”urn:oasis:names:tc:xacml:1.0:function:integer-bag”>
 <AttributeValue DataType=”http://www.w3.org/2001/XMLSchema#integer”>1</AttributeValue>
 <AttributeValue DataType=”http://www.w3.org/2001/XMLSchema#integer”>3</AttributeValue>
 <AttributeValue DataType=”http://www.w3.org/2001/XMLSchema#integer”>5</AttributeValue>
 <AttributeValue DataType=”http://www.w3.org/2001/XMLSchema#integer”>19</AttributeValue>
 </Apply>
 </Apply>
 This expression is “True” because each of the elements of the first bag is greater than at
 least one of the elements of the second bag.

 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.*;

import java.util.ArrayList;
import java.util.List;

public class AllOfAny extends XACMLFunction {

    public AllOfAny()  {
    }
    public FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException {
        // Initialize
        XACMLFunction func = null;
        DataBag bagOne = null;
        DataBag bagTwo = null;
        int args = getArgCount();
        // Validate the number of Function Arguments.
        if (args != 3) {
            throw new NotApplicableException("Not Correct Number of Arguments, must provide 3");
        }

        if ((getArg(0) == null) || (!(getArg(0) instanceof XACMLFunction))) {
            throw new NotApplicableException("AnyOfAll first argument is null or not a XACML Function");
        }
        if ((getArg(1) == null) || (!(getArg(1) instanceof XACMLFunction))) {
            throw new NotApplicableException("AnyOfAll second argument is null or not a Bag");
        }
        if ((getArg(2) == null) || (!(getArg(2) instanceof XACMLFunction))) {
            throw new NotApplicableException("AnyOfAll third argument is null or not a Bag");
        }
        // Cast our Arguments...
        func = (XACMLFunction) getArg(0);
        bagOne = (DataBag) getArg(1).evaluate(pip);
        bagTwo = (DataBag) getArg(2).evaluate(pip);

        // Create our Result List for Applying an And against all results.
        List<DataValue> results = new ArrayList<DataValue>();
        // Cast the Bag Value.
        List<DataValue> bagOneValues = bagOne.getValue(pip);
        // Iterate over our First Bag against our Second Bag.
        for (DataValue dataValue1 : bagOneValues) {
            dataValue1.evaluate(pip);
            // Perform the AnyOf Function.
            AnyOf anyOf = new AnyOf();
            anyOf.addArgument(func); // Apply Function
            anyOf.addArgument(dataValue1);
            anyOf.addArgument(bagTwo);
            FunctionArgument result = anyOf.evaluate(pip);
            if ((result == null) || (!(result instanceof DataValue))) {
                    throw new NotApplicableException("AllOfAny Resultant Function Argument is Invalid");
            }
            results.add((DataValue) result);
        } // End of  First Bag Loop.

        // Now Perform an And Function based upon all Results Received.
        And fAnd = new And();
        for(DataValue result : results) {
            fAnd.addArgument(result);
        }
        FunctionArgument result = fAnd.evaluate(pip);
        if ((result == null) || (!(result instanceof DataValue))) {
            throw new NotApplicableException("AllOfAny Resultant Function Argument is Invalid");
        }
        // Return final And Resultant
        return result;
    }
}
