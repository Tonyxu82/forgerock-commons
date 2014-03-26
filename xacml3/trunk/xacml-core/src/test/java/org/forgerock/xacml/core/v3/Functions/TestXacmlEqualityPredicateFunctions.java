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
import org.forgerock.xacml.core.v3.engine.XACML3PrivilegeUtils;
import org.forgerock.xacml.core.v3.model.DataType;
import org.forgerock.xacml.core.v3.model.DataValue;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.forgerock.xacml.core.v3.model.YearMonthDuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


/**
 * XACML Equality Predicate Functions
 * <p/>
 * Testing Functions as specified by OASIS XACML v3 Core specification.
 *
 * X500Name
 */
public class TestXacmlEqualityPredicateFunctions {

    static final FunctionArgument trueObject = new DataValue(DataType.XACMLBOOLEAN, "true");
    static final FunctionArgument falseObject = new DataValue(DataType.XACMLBOOLEAN, "false");


    @BeforeClass
    public void before() throws Exception {
    }

    @AfterClass
    public void after() throws Exception {
    }

    @Test
    public void testAnyuriEqual() throws XACML3EntitlementException {
        FunctionArgument anyuri1 = new DataValue(DataType.XACMLANYURI, "/openam/xacml");
        FunctionArgument anyuri2 = new DataValue(DataType.XACMLANYURI, "/a/b/c/e/f");
        FunctionArgument anyuri3 = new DataValue(DataType.XACMLANYURI, "/");
        FunctionArgument anyuri4 = new DataValue(DataType.XACMLANYURI, "/a/b/c/e/f");

        AnyuriEqual anyuriEqual = new AnyuriEqual();
        // Place Objects in Argument stack for comparison.
        anyuriEqual.addArgument(anyuri1);
        anyuriEqual.addArgument(anyuri2);
        FunctionArgument result = anyuriEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        anyuriEqual = new AnyuriEqual();
        // Place Objects in Argument stack for comparison.
        anyuriEqual.addArgument(anyuri1);
        anyuriEqual.addArgument(anyuri3);
        result = anyuriEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        anyuriEqual = new AnyuriEqual();
        // Place Objects in Argument stack for comparison.
        anyuriEqual.addArgument(anyuri2);
        anyuriEqual.addArgument(anyuri4);
        result = anyuriEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testBase64BinaryEqual() throws XACML3EntitlementException {

        // base64data1 and base64data2 contained the Base 64 encoding of:
        // ForgeRock - OpenAM XACML says Hello!
        FunctionArgument base64data1 = new DataValue(DataType.XACMLBASE64BINARY,
                "Rm9yZ2VSb2NrIC0gT3BlbkFNIFhBQ01MIHNheXMgSGVsbG8h");
        // This is a very small Test!
        FunctionArgument base64data2 = new DataValue(DataType.XACMLBASE64BINARY,
                "VGhpcyBpcyBhIHZlcnkgc21hbGwgVGVzdCE=");
        // This is a very small Test as well!
        FunctionArgument base64data3 = new DataValue(DataType.XACMLBASE64BINARY,
                "VGhpcyBpcyBhIHZlcnkgc21hbGwgVGVzdCBhcyB3ZWxsIQ==");
        // ForgeRock - OpenAM XACML says Hello!
        FunctionArgument base64data4 = new DataValue(DataType.XACMLBASE64BINARY,
                "Rm9yZ2VSb2NrIC0gT3BlbkFNIFhBQ01MIHNheXMgSGVsbG8h");

        Base64BinaryEqual base64binaryEqual = new Base64BinaryEqual();
        base64binaryEqual.addArgument(base64data1);
        base64binaryEqual.addArgument(base64data2);

        FunctionArgument result = base64binaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        base64binaryEqual = new Base64BinaryEqual();
        base64binaryEqual.addArgument(base64data1);
        base64binaryEqual.addArgument(base64data3);

        result = base64binaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        base64binaryEqual = new Base64BinaryEqual();
        base64binaryEqual.addArgument(base64data1);
        base64binaryEqual.addArgument(base64data4);

        result = base64binaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testBooleanEqual() throws XACML3EntitlementException {
        BooleanEqual booleanEqual = new BooleanEqual();
        // Place Objects in Argument stack for comparison.
        booleanEqual.addArgument(trueObject);
        booleanEqual.addArgument(falseObject);
        FunctionArgument result = booleanEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        booleanEqual = new BooleanEqual();
        // Place Objects in Argument stack for comparison.
        booleanEqual.addArgument(falseObject);
        booleanEqual.addArgument(falseObject);
        result = booleanEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        booleanEqual = new BooleanEqual();
        // Place Objects in Argument stack for comparison.
        booleanEqual.addArgument(trueObject);
        booleanEqual.addArgument(trueObject);
        result = booleanEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testDateEqual() throws XACML3EntitlementException {

        Date date1 = XACML3PrivilegeUtils.stringToDate("2013-03-11");
        FunctionArgument dateObject1 = new DataValue(DataType.XACMLDATE, date1, true);

        Date date2 = XACML3PrivilegeUtils.stringToDate("2013-03-12");
        FunctionArgument dateObject2 = new DataValue(DataType.XACMLDATE, date2, true);

        Date date3 = XACML3PrivilegeUtils.stringToDate("2013-03-11");
        FunctionArgument dateObject3 = new DataValue(DataType.XACMLDATE, date3, true);

        Date date4 = XACML3PrivilegeUtils.stringToDate("2014-03-11");
        FunctionArgument dateObject4 = new DataValue(DataType.XACMLDATE, date4, true);


        DateEqual dateEqual = new DateEqual();
        dateEqual.addArgument(dateObject1);
        dateEqual.addArgument(dateObject2);

        FunctionArgument result = dateEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        dateEqual = new DateEqual();
        dateEqual.addArgument(dateObject1);
        dateEqual.addArgument(dateObject3);

        result = dateEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        dateEqual = new DateEqual();
        dateEqual.addArgument(dateObject3);
        dateEqual.addArgument(dateObject4);

        result = dateEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

    }

    @Test
    public void testDatetimeEqual() throws XACML3EntitlementException {
        Date date1 = XACML3PrivilegeUtils.stringToDateTime("2013-03-11:01:45:30.126");
        FunctionArgument dateObject1 = new DataValue(DataType.XACMLDATETIME, date1, true);

        Date date2 = XACML3PrivilegeUtils.stringToDateTime("2013-03-11:01:45:30.124");
        FunctionArgument dateObject2 = new DataValue(DataType.XACMLDATETIME, date2, true);

        Date date3 = XACML3PrivilegeUtils.stringToDateTime("2013-03-11:01:45:30.126");
        FunctionArgument dateObject3 = new DataValue(DataType.XACMLDATETIME, date3, true);

        Date date4 = XACML3PrivilegeUtils.stringToDateTime("2014-03-11:01:45:30.126");
        FunctionArgument dateObject4 = new DataValue(DataType.XACMLDATETIME, date4, true);

        DatetimeEqual dateTimeEqual = new DatetimeEqual();
        dateTimeEqual.addArgument(dateObject1);
        dateTimeEqual.addArgument(dateObject2);

        FunctionArgument result = dateTimeEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        dateTimeEqual = new DatetimeEqual();
        dateTimeEqual.addArgument(dateObject1);
        dateTimeEqual.addArgument(dateObject3);

        result = dateTimeEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        dateTimeEqual = new DatetimeEqual();
        dateTimeEqual.addArgument(dateObject3);
        dateTimeEqual.addArgument(dateObject4);

        result = dateTimeEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

    }

    @Test
    public void testDaytimedurationEqual() throws XACML3EntitlementException {

        Long duration1 = XACML3PrivilegeUtils.stringDayTimeDurationToLongDuration("011:01:45:30.126");
        FunctionArgument dateObject1 = new DataValue(DataType.XACMLDAYTIMEDURATION, duration1, true);

        Long duration2 = XACML3PrivilegeUtils.stringDayTimeDurationToLongDuration("012:01:45:30.124");
        FunctionArgument dateObject2 = new DataValue(DataType.XACMLDAYTIMEDURATION, duration2, true);

        Long duration3 = XACML3PrivilegeUtils.stringDayTimeDurationToLongDuration("011:01:45:30.126");
        FunctionArgument dateObject3 = new DataValue(DataType.XACMLDAYTIMEDURATION, duration3, true);

        Long duration4 = XACML3PrivilegeUtils.stringDayTimeDurationToLongDuration("001:01:45:30.126");
        FunctionArgument dateObject4 = new DataValue(DataType.XACMLDAYTIMEDURATION, duration4, true);


        DaytimedurationEqual dateTimeDurationEqual = new DaytimedurationEqual();
        dateTimeDurationEqual.addArgument(dateObject1);
        dateTimeDurationEqual.addArgument(dateObject2);

        FunctionArgument result = dateTimeDurationEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        dateTimeDurationEqual = new DaytimedurationEqual();
        dateTimeDurationEqual.addArgument(dateObject1);
        dateTimeDurationEqual.addArgument(dateObject3);

        result = dateTimeDurationEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        dateTimeDurationEqual = new DaytimedurationEqual();
        dateTimeDurationEqual.addArgument(dateObject1);
        dateTimeDurationEqual.addArgument(dateObject4);

        result = dateTimeDurationEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

    }

    @Test
    public void testDoubleEqual() throws XACML3EntitlementException {
        FunctionArgument double1 = new DataValue(DataType.XACMLDOUBLE, 2111111111111111111290876D, true);
        FunctionArgument double2 = new DataValue(DataType.XACMLDOUBLE, 456789D, true);
        FunctionArgument double3 = new DataValue(DataType.XACMLDOUBLE, 2111111111111111111290876D, true);
        FunctionArgument double4 = new DataValue(DataType.XACMLDOUBLE, 2D, true);

        DoubleEqual doubleEqual = new DoubleEqual();
        doubleEqual.addArgument(double1);
        doubleEqual.addArgument(double2);

        FunctionArgument result = doubleEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        doubleEqual = new DoubleEqual();
        doubleEqual.addArgument(double1);
        doubleEqual.addArgument(double3);

        result = doubleEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        doubleEqual = new DoubleEqual();
        doubleEqual.addArgument(double1);
        doubleEqual.addArgument(double4);

        result = doubleEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());
    }

    @Test
    public void testHexbinaryEqual() throws XACML3EntitlementException {
        FunctionArgument hexdata1 = new DataValue(DataType.XACMLHEXBINARY, "0123456789abcdef");
        FunctionArgument hexdata2 = new DataValue(DataType.XACMLHEXBINARY, "FF");
        FunctionArgument hexdata3 = new DataValue(DataType.XACMLHEXBINARY, "0123456789ABCDEF");
        FunctionArgument hexdata4 = new DataValue(DataType.XACMLHEXBINARY, "06F2");
        FunctionArgument hexdata5 = new DataValue(DataType.XACMLHEXBINARY, "CED");

        HexbinaryEqual hexbinaryEqual = new HexbinaryEqual();
        hexbinaryEqual.addArgument(hexdata1);
        hexbinaryEqual.addArgument(hexdata2);

        FunctionArgument result = hexbinaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        hexbinaryEqual = new HexbinaryEqual();
        hexbinaryEqual.addArgument(hexdata1);
        hexbinaryEqual.addArgument(hexdata3);

        result = hexbinaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        hexbinaryEqual = new HexbinaryEqual();
        hexbinaryEqual.addArgument(hexdata1);
        hexbinaryEqual.addArgument(hexdata4);

        result = hexbinaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        hexbinaryEqual = new HexbinaryEqual();
        hexbinaryEqual.addArgument(hexdata1);
        hexbinaryEqual.addArgument(hexdata5);

        result = hexbinaryEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());
    }

    @Test
    public void testIntegerEqual() throws XACML3EntitlementException {
        FunctionArgument integer1 = new DataValue(DataType.XACMLINTEGER, 22, true);
        FunctionArgument integer2 = new DataValue(DataType.XACMLINTEGER, 456789, true);
        FunctionArgument integer3 = new DataValue(DataType.XACMLINTEGER, 22, true);
        FunctionArgument integer4 = new DataValue(DataType.XACMLINTEGER, 0, true);

        IntegerEqual integerEqual = new IntegerEqual();
        integerEqual.addArgument(integer1);
        integerEqual.addArgument(integer2);

        FunctionArgument result = integerEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        integerEqual = new IntegerEqual();
        integerEqual.addArgument(integer1);
        integerEqual.addArgument(integer3);

        result = integerEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        integerEqual = new IntegerEqual();
        integerEqual.addArgument(integer1);
        integerEqual.addArgument(integer4);

        result = integerEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());
    }

    @Test
    public void testRfc822NameEqual() throws XACML3EntitlementException {
        FunctionArgument rfc822Name1 = new DataValue(DataType.XACMLRFC822NAME,
                "joe@example.org");
        FunctionArgument rfc822Name2 = new DataValue(DataType.XACMLRFC822NAME,
                "joe.smith@example.org");
        FunctionArgument rfc822Name3 = new DataValue(DataType.XACMLRFC822NAME,
                "joe.smith@example.org");
        FunctionArgument rfc822Name4 = new DataValue(DataType.XACMLRFC822NAME,
                "joe.smith@ExAmPlE.oRg");

        X500NameEqual rfc822NameEqual = new X500NameEqual();
        // Place Objects in Argument stack for comparison.
        rfc822NameEqual.addArgument(rfc822Name1);
        rfc822NameEqual.addArgument(rfc822Name2);
        FunctionArgument result = rfc822NameEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        rfc822NameEqual = new X500NameEqual();
        // Place Objects in Argument stack for comparison.
        rfc822NameEqual.addArgument(rfc822Name1);
        rfc822NameEqual.addArgument(rfc822Name3);
        result = rfc822NameEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        rfc822NameEqual = new X500NameEqual();
        // Place Objects in Argument stack for comparison.
        rfc822NameEqual.addArgument(rfc822Name2);
        rfc822NameEqual.addArgument(rfc822Name4);
        result = rfc822NameEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testStringEqual() throws XACML3EntitlementException {
        FunctionArgument string1 = new DataValue(DataType.XACMLSTRING, "Hello World!");
        FunctionArgument string2 = new DataValue(DataType.XACMLSTRING, "HELLO WORLD!");
        FunctionArgument string3 = new DataValue(DataType.XACMLSTRING, "Hello");
        FunctionArgument string4 = new DataValue(DataType.XACMLSTRING, "HELLO WORLD!");

        StringEqual stringEqual = new StringEqual();
        // Place Objects in Argument stack for comparison.
        stringEqual.addArgument(string1);
        stringEqual.addArgument(string2);
        FunctionArgument result = stringEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        stringEqual = new StringEqual();
        // Place Objects in Argument stack for comparison.
        stringEqual.addArgument(string1);
        stringEqual.addArgument(string3);
        result = stringEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        stringEqual = new StringEqual();
        // Place Objects in Argument stack for comparison.
        stringEqual.addArgument(string2);
        stringEqual.addArgument(string4);
        result = stringEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testStringEqualIgnoreCase() throws XACML3EntitlementException {
        FunctionArgument string1 = new DataValue(DataType.XACMLSTRING, "Hello World!");
        FunctionArgument string2 = new DataValue(DataType.XACMLSTRING, "HELLO WORLD!");
        FunctionArgument string3 = new DataValue(DataType.XACMLSTRING, "Hello World");
        FunctionArgument string4 = new DataValue(DataType.XACMLSTRING, "HELLO WORLD!");

        StringEqualIgnoreCase stringEqualIgnoreCase = new StringEqualIgnoreCase();
        // Place Objects in Argument stack for comparison.
        stringEqualIgnoreCase.addArgument(string1);
        stringEqualIgnoreCase.addArgument(string2);
        FunctionArgument result = stringEqualIgnoreCase.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        stringEqualIgnoreCase = new StringEqualIgnoreCase();
        // Place Objects in Argument stack for comparison.
        stringEqualIgnoreCase.addArgument(string1);
        stringEqualIgnoreCase.addArgument(string3);
        result = stringEqualIgnoreCase.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        stringEqualIgnoreCase = new StringEqualIgnoreCase();
        // Place Objects in Argument stack for comparison.
        stringEqualIgnoreCase.addArgument(string1);
        stringEqualIgnoreCase.addArgument(string4);
        result = stringEqualIgnoreCase.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testTimeEqual() throws XACML3EntitlementException {
        Date time1 = XACML3PrivilegeUtils.stringToTime("01:45:30.126");
        FunctionArgument timeObject1 = new DataValue(DataType.XACMLTIME, time1, true);

        Date time2 = XACML3PrivilegeUtils.stringToTime("02:45:30.126");
        FunctionArgument timeObject2 = new DataValue(DataType.XACMLTIME, time2, true);

        Date time3 = XACML3PrivilegeUtils.stringToTime("01:45:30.126");
        FunctionArgument timeObject3 = new DataValue(DataType.XACMLTIME, time3, true);

        Date time4 = XACML3PrivilegeUtils.stringToTime("01:45:30.127");
        FunctionArgument timeObject4 = new DataValue(DataType.XACMLTIME, time4, true);

        TimeEqual timeEqual = new TimeEqual();
        timeEqual.addArgument(timeObject1);
        timeEqual.addArgument(timeObject2);

        FunctionArgument result = timeEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        timeEqual = new TimeEqual();
        timeEqual.addArgument(timeObject1);
        timeEqual.addArgument(timeObject3);

        result = timeEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        timeEqual = new TimeEqual();
        timeEqual.addArgument(timeObject3);
        timeEqual.addArgument(timeObject4);

        result = timeEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());
    }

    @Test
    public void testX500NameEqual() throws XACML3EntitlementException {
        FunctionArgument x500Name1 = new DataValue(DataType.XACMLX500NAME,
                "/c=us/o=ForgeRock/ou=Components/cn=OpenAM");
        FunctionArgument x500Name2 = new DataValue(DataType.XACMLX500NAME,
                "/c=us/o=ForgeRock/ou=People/cn=Bob Smith");
        FunctionArgument x500Name3 = new DataValue(DataType.XACMLX500NAME,
                "/cn=Bob Smith");
        FunctionArgument x500Name4 = new DataValue(DataType.XACMLX500NAME,
                "/c=us/o=ForgeRock/ou=People/cn=Bob Smith");

        X500NameEqual x500NameEqual = new X500NameEqual();
        // Place Objects in Argument stack for comparison.
        x500NameEqual.addArgument(x500Name1);
        x500NameEqual.addArgument(x500Name2);
        FunctionArgument result = x500NameEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        x500NameEqual = new X500NameEqual();
        // Place Objects in Argument stack for comparison.
        x500NameEqual.addArgument(x500Name1);
        x500NameEqual.addArgument(x500Name3);
        result = x500NameEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        x500NameEqual = new X500NameEqual();
        // Place Objects in Argument stack for comparison.
        x500NameEqual.addArgument(x500Name2);
        x500NameEqual.addArgument(x500Name4);
        result = x500NameEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testYearmonthdurationEqual() throws XACML3EntitlementException {
        YearMonthDuration duration1 = new YearMonthDuration("0020-03");
        FunctionArgument dateObject1 = new DataValue(DataType.XACMLYEARMONTHDURATION, duration1, true);

        YearMonthDuration duration2 = new YearMonthDuration("0016-03");
        FunctionArgument dateObject2 = new DataValue(DataType.XACMLYEARMONTHDURATION, duration2, true);

        YearMonthDuration duration3 = new YearMonthDuration("0013-03");
        FunctionArgument dateObject3 = new DataValue(DataType.XACMLYEARMONTHDURATION, duration3, true);

        YearMonthDuration duration4 = new YearMonthDuration("0020-03");
        FunctionArgument dateObject4 = new DataValue(DataType.XACMLYEARMONTHDURATION, duration4, true);

        YearmonthdurationEqual yearmonthdurationEqual = new YearmonthdurationEqual();
        yearmonthdurationEqual.addArgument(dateObject1);
        yearmonthdurationEqual.addArgument(dateObject2);

        FunctionArgument result = yearmonthdurationEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        yearmonthdurationEqual = new YearmonthdurationEqual();
        yearmonthdurationEqual.addArgument(dateObject1);
        yearmonthdurationEqual.addArgument(dateObject4);

        result = yearmonthdurationEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        yearmonthdurationEqual = new YearmonthdurationEqual();
        yearmonthdurationEqual.addArgument(dateObject3);
        yearmonthdurationEqual.addArgument(dateObject4);

        result = yearmonthdurationEqual.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

    }

    @Test
    public void testIPAddressEqual() throws XACML3EntitlementException {
        FunctionArgument anyIP1 = new DataValue(DataType.XACMLIPADDRESS, "10.0.1.2");
        FunctionArgument anyIP2 = new DataValue(DataType.XACMLIPADDRESS, "10.0.1.12");
        FunctionArgument anyIP3 = new DataValue(DataType.XACMLIPADDRESS, "10.0.1.42");
        FunctionArgument anyIP4 = new DataValue(DataType.XACMLIPADDRESS, "10.0.12.2");
        FunctionArgument anyIP5 = new DataValue(DataType.XACMLIPADDRESS, "10.0.12.2");
        FunctionArgument anyIP6 = new DataValue(DataType.XACMLIPADDRESS, "fdff:cafe:babe:cab1:426c:8fff:fe2c:1e65");
        FunctionArgument anyIP7 = new DataValue(DataType.XACMLIPADDRESS, "fdff:cafe:babe:cab1:426c:8fff:fe2c:1e65");

        IPAddressEqual fEquals = new IPAddressEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyIP1);
        fEquals.addArgument(anyIP2);
        FunctionArgument result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        fEquals = new IPAddressEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyIP1);
        fEquals.addArgument(anyIP3);
        result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        fEquals = new IPAddressEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyIP4);
        fEquals.addArgument(anyIP5);
        result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        fEquals = new IPAddressEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyIP6);
        fEquals.addArgument(anyIP7);
        result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }

    @Test
    public void testIDNSNameEqual() throws XACML3EntitlementException {
        FunctionArgument anyDNSName1 = new DataValue(DataType.XACMLDNSNAME, "example.org");
        FunctionArgument anyDNSName2 = new DataValue(DataType.XACMLDNSNAME, "www.example.org");
        FunctionArgument anyDNSName3 = new DataValue(DataType.XACMLDNSNAME, "test.example.org");
        FunctionArgument anyDNSName4 = new DataValue(DataType.XACMLDNSNAME, "openam.example.org");
        FunctionArgument anyDNSName5 = new DataValue(DataType.XACMLDNSNAME, "openam.example.org");
        FunctionArgument anyDNSName6 = new DataValue(DataType.XACMLDNSNAME, "openam.example.co.uk");
        FunctionArgument anyDNSName7 = new DataValue(DataType.XACMLDNSNAME, "openam.example.co.uk");

        DNSNameEqual fEquals = new DNSNameEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyDNSName1);
        fEquals.addArgument(anyDNSName2);
        FunctionArgument result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        fEquals = new DNSNameEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyDNSName1);
        fEquals.addArgument(anyDNSName3);
        result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isFalse());

        fEquals = new DNSNameEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyDNSName4);
        fEquals.addArgument(anyDNSName5);
        result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

        fEquals = new DNSNameEqual();
        // Place Objects in Argument stack for comparison.
        fEquals.addArgument(anyDNSName6);
        fEquals.addArgument(anyDNSName7);
        result = fEquals.evaluate(null);
        assertNotNull(result);
        assertTrue(result.isTrue());

    }



}
