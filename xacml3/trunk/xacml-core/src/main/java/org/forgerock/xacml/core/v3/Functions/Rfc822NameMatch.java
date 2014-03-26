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
 *  A.3.14 Special match functions
 These functions operate on various types and evaluate to “http://www.w3.org/2001/XMLSchema#boolean”
 based on the specified standard matching algorithm.

 urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match
 This function SHALL take two arguments, the first is of data-type “http://www.w3.org/2001/XMLSchema#string”
 and the second is of data-type “urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name” and SHALL
 return an “http://www.w3.org/2001/XMLSchema#boolean”.

 This function SHALL evaluate to "True" if the first argument
 matches the second argument according to the following specification.
 An RFC822 name consists of a local-part followed by "@" followed by a domain-part.

 The local-part is case-sensitive, while the domain-part (which is usually a DNS name) is not case-sensitive.
 The second argument contains a complete rfc822Name.

 The first argument is a complete or partial rfc822Name used to select appropriate values
 in the second argument as follows.

 In order to match a particular address in the second argument,
 the first argument must specify the complete mail address to be matched.

 For example, if the first argument is “Anderson@sun.com”, this matches a value in the second argument of
 “Anderson@sun.com” and “Anderson@SUN.COM”, but not “Anne.Anderson@sun.com”,
 “anderson@sun.com” or “Anderson@east.sun.com”.

 In order to match any address at a particular domain in the second argument, the first argument must specify
 only a domain name (usually a DNS name).

 For example, if the first argument is “sun.com”,
 this matches a value in the second argument of “Anderson@sun.com” or “Baxter@SUN.COM”, but not “Anderson@east.sun.com”.

 In order to match any address in a particular domain in the second argument, the
 first argument must specify the desired domain-part with a leading ".".

 For example, if the first argument is “.east.sun.com”, this matches a value in the second argument of
 "Anderson@east.sun.com" and "anne.anderson@ISRG.EAST.SUN.COM" but not "Anderson@sun.com".

 */


import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.DataValue;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.forgerock.xacml.core.v3.model.XACMLFunction;

/**
 * urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match
 */
public class Rfc822NameMatch extends XACMLFunction {

    public Rfc822NameMatch() {
    }

    public FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException {

        FunctionArgument retVal = FunctionArgument.falseObject;
        // Validate argument List
        if (getArgCount() != 2) {
            return retVal;
        }
        // Check and Cast arguments.
        DataValue cop_rfc822NameValue = (DataValue) getArg(0).doEvaluate(pip);
        DataValue rfc822NameValue = (DataValue) getArg(1).doEvaluate(pip);
        if ((cop_rfc822NameValue == null) || (rfc822NameValue == null)) {
            throw new XACML3EntitlementException("No Pattern or Data Value Specified");
        }
        try {
            // Verify the rfc822 Names provided.
            // Split at the @ sign.
            String[] cop_rfc822NameString = cop_rfc822NameValue.asRfc822Name(pip).split("@");
            String[] rfc822NameString = rfc822NameValue.asRfc822Name(pip).split("@");
            if ((cop_rfc822NameString == null) || (cop_rfc822NameString.length > 2) ||
                    (cop_rfc822NameString.length < 0)) {
                return retVal;
            }
            if ((rfc822NameString == null) || (rfc822NameString.length != 2)) {
                return retVal;
            }

            // Now based upon the length of the Complete or partial rfcName, perform necessary checks.
            if (cop_rfc822NameString.length == 2) {
                // Check if the Complete or Partial rfc822 Name is contained within the full rfc822 Name
                if ( (rfc822NameString[0].equals(cop_rfc822NameString[0])) &&
                     (rfc822NameString[1].equalsIgnoreCase(cop_rfc822NameString[1])) ) {
                    retVal = FunctionArgument.trueObject;
                }
            } else {
                // We have a Partial Name, 1 entry in the Array,
                // Check Partial rfc822 Domain Name is contained within the full rfc822 Name
                if (cop_rfc822NameString[0].startsWith(".")) {
                    if (rfc822NameString[1].toLowerCase().contains(cop_rfc822NameString[0].toLowerCase())) {
                        retVal = FunctionArgument.trueObject;
                    }
                } else {
                    if (rfc822NameString[1].equalsIgnoreCase(cop_rfc822NameString[0])) {
                        retVal = FunctionArgument.trueObject;
                    }
                }
            }
        } catch (Exception e) {
            throw new XACML3EntitlementException("RFC822Name Exception: " + e.getMessage());
        }
        return retVal;
    }

}
