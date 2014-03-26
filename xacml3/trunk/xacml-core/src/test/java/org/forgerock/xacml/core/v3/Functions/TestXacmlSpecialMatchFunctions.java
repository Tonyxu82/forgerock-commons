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

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.model.DataType;
import org.forgerock.xacml.core.v3.model.DataValue;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 *  A.3.14 Special match functions
 These functions operate on various types and evaluate to “http://www.w3.org/2001/XMLSchema#boolean” based on the specified standard matching algorithm.

 urn:oasis:names:tc:xacml:1.0:function:x500Name-match
 This function shall take two arguments of "urn:oasis:names:tc:xacml:1.0:data-type:x500Name" and shall
 return an "http://www.w3.org/2001/XMLSchema#boolean".

 It shall return “True” if and only if the first argument matches some terminal sequence of RDNs
 from the second argument when compared using x500Name-equal.

 As an example (non-normative), if the first argument is “O=Medico Corp,C=US”
 and the second argument is “cn=John Smith,o=Medico Corp, c=US”, then the function will return “True”.

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

 In order to match any address at a particular domain in the second argument, the first argument must specify o
 nly a domain name (usually a DNS name).  For example, if the first argument is “sun.com”,
 this matches a value in the second argument of “Anderson@sun.com” or “Baxter@SUN.COM”, but not “Anderson@east.sun.com”.
 In order to match any address in a particular domain in the second argument, the
 first argument must specify the desired domain-part with a leading ".".  For example, if the first argument
 is “.east.sun.com”, this matches a value in the second argument of
 "Anderson@east.sun.com" and "anne.anderson@ISRG.EAST.SUN.COM" but not "Anderson@sun.com".

 */

/**
 * XACML Special Match Functions
 * <p/>
 * Testing Functions as specified by OASIS XACML v3 Core specification.
 *
 * X500Name
 */
public class TestXacmlSpecialMatchFunctions {

    /**
     * Boolean Results
     */
    static final FunctionArgument trueObject = new DataValue(DataType.XACMLBOOLEAN, "true");
    static final FunctionArgument falseObject = new DataValue(DataType.XACMLBOOLEAN, "false");

    /**
     * Directory Names
     */
    static final FunctionArgument x500Name1 = new DataValue(DataType.XACMLX500NAME,
            "/c=us/o=ForgeRock/ou=Components/cn=OpenAM");
    static final FunctionArgument x500Name2 = new DataValue(DataType.XACMLX500NAME,
            "/c=us/o=ForgeRock/ou=People/cn=Bob Smith");
    static final FunctionArgument x500Name3 = new DataValue(DataType.XACMLX500NAME,
            "/cn=Bob Smith");
    static final FunctionArgument x500Name4 = new DataValue(DataType.XACMLX500NAME,
            "/c=us/o=ForgeRock/ou=People/cn=Bob Smith");

    static final FunctionArgument LDAPName1 = new DataValue(DataType.XACMLX500NAME,
            "O=Medico Corp,C=US");

    static final FunctionArgument LDAPName2 = new DataValue(DataType.XACMLX500NAME,
            "cn=John Smith,o=Medico Corp, c=US");    // Notice Whitespace differences.

    /**
     * RFC822 Names
     */
    static final FunctionArgument rfc822Name1 = new DataValue(DataType.XACMLRFC822NAME,
            "joe@example.org");
    static final FunctionArgument rfc822Name2 = new DataValue(DataType.XACMLRFC822NAME,
            "joe.smith@example.org");
    static final FunctionArgument rfc822Name3 = new DataValue(DataType.XACMLRFC822NAME,
            "Joe.Smith@example.org");
    static final FunctionArgument rfc822Name4 = new DataValue(DataType.XACMLRFC822NAME,
            "joe.smith@ExAmPlE.oRg");

    static final FunctionArgument rfc822Name5 = new DataValue(DataType.XACMLRFC822NAME,
            "Anderson@sun.com");
    static final FunctionArgument rfc822Name6 = new DataValue(DataType.XACMLRFC822NAME,
            "Anderson@SUN.COM");
    static final FunctionArgument rfc822Name7 = new DataValue(DataType.XACMLRFC822NAME,
            "Anne.Anderson@sun.com");
    static final FunctionArgument rfc822Name8 = new DataValue(DataType.XACMLRFC822NAME,
            "anderson@sun.com");
    static final FunctionArgument rfc822Name9 = new DataValue(DataType.XACMLRFC822NAME,
            "Anderson@east.sun.com");
    static final FunctionArgument rfc822NameA = new DataValue(DataType.XACMLRFC822NAME,
            ".east.sun.com");
    static final FunctionArgument rfc822NameB = new DataValue(DataType.XACMLRFC822NAME,
            "anne.anderson@ISRG.EAST.SUN.COM");


    @BeforeClass
    public void before() throws Exception {
    }

    @AfterClass
    public void after() throws Exception {
    }

    /**
     * urn:oasis:names:tc:xacml:1.0:function:x500Name-match
     */
    @Test
    public void testX500NameMatch() throws XACML3EntitlementException {

        X500NameMatch nameMatch = new X500NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(x500Name3);
        nameMatch.addArgument(x500Name2);
        FunctionArgument result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        nameMatch = new X500NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(x500Name3);
        nameMatch.addArgument(x500Name4);
        result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        nameMatch = new X500NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(x500Name1);
        nameMatch.addArgument(x500Name2);
        result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertFalse(result.asBoolean(null));

        nameMatch = new X500NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(LDAPName1);
        nameMatch.addArgument(LDAPName2);
        result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    /**
     * urn:oasis:names:tc:xacml:1.0:function:x500Name-match
     */
    @Test
    public void testX500NameMatch_False_Condition() throws XACML3EntitlementException {

        X500NameMatch nameMatch = new X500NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(x500Name1);
        nameMatch.addArgument(x500Name2);
        FunctionArgument result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertFalse(result.asBoolean(null));

    }

    /**
     * urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match
     */
    @Test
    public void testRFC822NameMatch() throws XACML3EntitlementException {

        Rfc822NameMatch nameMatch = new Rfc822NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(rfc822Name5);
        nameMatch.addArgument(rfc822Name5);
        FunctionArgument result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        nameMatch = new Rfc822NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(rfc822Name5);
        nameMatch.addArgument(rfc822Name6);
        result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    /**
     * urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match
     */
    @Test
    public void testRFC822NameMatch_False_Conditions() throws XACML3EntitlementException {

        Rfc822NameMatch nameMatch = new Rfc822NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(rfc822Name5);
        nameMatch.addArgument(rfc822Name7);
        FunctionArgument result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertFalse(result.asBoolean(null));

        nameMatch = new Rfc822NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(rfc822Name5);
        nameMatch.addArgument(rfc822Name8);
        result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertFalse(result.asBoolean(null));

        nameMatch = new Rfc822NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(rfc822Name5);
        nameMatch.addArgument(rfc822Name9);
        result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertFalse(result.asBoolean(null));

    }


    /**
     * urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match
     */
    @Test
    public void testRFC822NameMatch_PartialDomain() throws XACML3EntitlementException {

        Rfc822NameMatch nameMatch = new Rfc822NameMatch();
        // Place Objects in Argument stack for comparison.
        nameMatch.addArgument(rfc822NameA);
        nameMatch.addArgument(rfc822NameB);
        FunctionArgument result = nameMatch.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }


}
