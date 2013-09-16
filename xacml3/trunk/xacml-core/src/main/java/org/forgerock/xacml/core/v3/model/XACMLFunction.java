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
package org.forgerock.xacml.core.v3.model;

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


/**
 * This interface defines the XACML functions.
 * <p/>
 * The Syntax, is to create a function object with a ResourceID, and a Value,
 * which will be checked when the function is evaluated.
 *
 * @author allan.foster@forgerock.com
 */
public abstract class XACMLFunction extends FunctionArgument {
    /**
     * Status Code Constants
     */
    public static final String URN_SYNTAX_ERROR = "urn:oasis:names:tc:xacml:1.0:status:syntax-error";
    public static final String URN_PROCESSING_ERROR = "urn:oasis:names:tc:xacml:1.0:status:processing-error";

    public static String spaces = "                                                                                                                                                                             ";

    /**
     * Globals
     */
    static java.util.Map<String, String> functions;
    protected List<FunctionArgument> arguments;
    protected String functionID;

    /**
     * Default Constructor
     */
    public XACMLFunction() {
        arguments = new ArrayList<FunctionArgument>();
    }

    /**
     * Set Function ID
     *
     * @param functionID
     */
    public void setFunctionID(String functionID) {
        this.functionID = functionID;
    }

    /**
     * Clear All Arguments
     *
     * @return
     */
    public XACMLFunction clearArguments() {
        arguments.clear();
        return this;
    }

    /**
     * Add a single Argument.
     *
     * @param arg
     * @return
     */
    public XACMLFunction addArgument(FunctionArgument arg) {
        arguments.add(arg);
        return this;
    }

    /**
     * Add a Collection of Function SArguments.
     *
     * @param args
     * @return
     */
    public XACMLFunction addArgument(List<FunctionArgument> args) {
        arguments.addAll(args);
        return this;
    }

    /**
     * Obtain Argument Value.
     *
     * @param pip
     * @return Object - representing argument value
     * @throws org.forgerock.xacml.core.v3.engine.XACML3EntitlementException
     */
    public Object getValue(XACMLEvalContext pip) throws XACML3EntitlementException {
        return doEvaluate(pip).getValue(pip);
    }

    /**
     * PEP Evaluate Method to obtain a evaluation result.
     *
     * @param pip
     * @return
     * @throws XACML3EntitlementException
     */
    public abstract FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException;

    private String functionName() {
        return functionID.substring(functionID.lastIndexOf(':')+1);
    }
    public String printDebugItem() {
        return " " + functionName()  ;
    }

    private String printIndent() {
        return  spaces.substring(0,indent);
    }

    public void printDebugEntry() {

        System.out.print(printIndent() + functionName()+ "( ");
        String sep = " ";
        for (FunctionArgument f : arguments) {
            System.out.print(sep +f.printDebugItem());
            sep = ", ";
        }
        System.out.println(")");
        indent+= 2;
    }
    public void printDebugExit( FunctionArgument res) {

        indent-= 2;
        System.out.print(printIndent() +"=" +res.printDebugItem());
        System.out.println("");
    }


    /**
     * PEP Evaluate Method to obtain a evaluation result.
     *
     * @param pip
     * @return
     * @throws XACML3EntitlementException
     */
    public FunctionArgument doEvaluate(XACMLEvalContext pip) throws XACML3EntitlementException {
        FunctionArgument result = null;
        printDebugEntry();
        try {
            result = evaluate(pip);
        } catch (XACML3EntitlementException ex)  {
            System.out.println("!!Exception");
            throw ex;
        } finally  {
            printDebugExit( result) ;
        }
        return result;
    };

    /**
     * Protected methods only for subclasses to
     * manipulate the Arguments.
     */
    protected FunctionArgument getArg(int index) {
        return arguments.get(index);
    }

    protected final List<FunctionArgument> getArguments() {
        return arguments;
    }

    /**
     * Required this as Public to properly interrogate Number of Arguments.
     *
     * @return int -- Number of Current Arguments.
     */
    public int getArgCount() {
        return arguments.size();
    }

    /**
     * Obtain the JSONObject for this Function's Implementation.
     *
     * @return
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = super.toJSONObject();

        jo.put("functionID", functionID);
        for (FunctionArgument arg : arguments) {
            jo.append("arguments", arg.toJSONObject());
        }

        return jo;

    }

    /**
     * Initialize the JSON Object's Arguments Array.
     *
     * @param jo - JSONObject
     * @throws JSONException
     */
    protected void init(JSONObject jo) throws JSONException {
        super.init(jo);
        functionID = jo.optString("functionID");

        JSONArray array = jo.optJSONArray("arguments");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = (JSONObject) array.get(i);
                arguments.add(FunctionArgument.getInstance(json));
            }
        }
        return;
    }

    ;

    /**
     * Perform a String to XML  Allow Matching Functions.
     *
     * @param type
     * @return String - XML String Object.
     */
    public String toXMLAllow(String type) {
        String retVal = "";
        /*
             Handle Match AnyOf and AllOf specially
        */

        if (type.equals("Match")) {
            retVal = "<Match MatchId=\"" + functionID + "\">";
        } else if (type.equals("Allow")) {
            retVal = "<Allow FunctionId=\"" + functionID + "\">";
        }
        for (FunctionArgument arg : arguments) {
            retVal = retVal + arg.toXML(type);
        }
        if (type.equals("Match")) {
            retVal = "</Match\">";
        } else if (type.equals("Allow")) {
            retVal = "</Allow\">";
        }
        return retVal;
    }

    /**
     * Obtain the instance of the specific Function by URN.
     *
     * @param name - URN
     * @return XACMLFunction
     */
    public static XACMLFunction getInstance(String name) {
        if (functions == null) {
            initFunctionTable();
        }
        String cName = functions.get(name);
        XACMLFunction retVal = null;
        if (cName == null) {
            cName = "org.forgerock.xacml.core.v3.Functions.Unimplemented";
        }
        try {
            retVal = (XACMLFunction) Class.forName(cName).newInstance();
        } catch (Exception ex) {
            retVal = null;
        }
        if (retVal != null) {
            retVal.setFunctionID(name);
            retVal.setType(DataType.XACMLUNDEFINED);  // TODO: Do Function need types?
        }
        return retVal;
    }

    /**
     * Initialize our URN to Java Function Mapping.
     */
    static void initFunctionTable() {
        functions = new HashMap<String, String>();

        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-equal", "org.forgerock.xacml.core.v3.Functions.StringEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-equal", "org.forgerock.xacml.core.v3.Functions.BooleanEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-equal", "org.forgerock.xacml.core.v3.Functions.IntegerEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-equal", "org.forgerock.xacml.core.v3.Functions.DoubleEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-equal", "org.forgerock.xacml.core.v3.Functions.DateEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-equal", "org.forgerock.xacml.core.v3.Functions.TimeEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-equal", "org.forgerock.xacml.core.v3.Functions.DatetimeEqual");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-equal", "org.forgerock.xacml.core.v3.Functions.DaytimedurationEqual");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-equal", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationEqual");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-equal-ignore-case", "org.forgerock.xacml.core.v3.Functions.StringEqualIgnoreCase");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-equal", "org.forgerock.xacml.core.v3.Functions.AnyuriEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-equal", "org.forgerock.xacml.core.v3.Functions.X500NameEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-equal", "org.forgerock.xacml.core.v3.Functions.Rfc822NameEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-equal", "org.forgerock.xacml.core.v3.Functions.HexbinaryEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-equal",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:DNSName-equal",
                "org.forgerock.xacml.core.v3.Functions.DNSNameEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:IPAddress-equal",
                "org.forgerock.xacml.core.v3.Functions.IPAddressEqual");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-add", "org.forgerock.xacml.core.v3.Functions.IntegerAdd");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-add", "org.forgerock.xacml.core.v3.Functions.DoubleAdd");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-subtract", "org.forgerock.xacml.core.v3.Functions.IntegerSubtract");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-subtract", "org.forgerock.xacml.core.v3.Functions.DoubleSubtract");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-multiply", "org.forgerock.xacml.core.v3.Functions.IntegerMultiply");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-multiply", "org.forgerock.xacml.core.v3.Functions.DoubleMultiply");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-divide", "org.forgerock.xacml.core.v3.Functions.IntegerDivide");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-divide", "org.forgerock.xacml.core.v3.Functions.DoubleDivide");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-mod", "org.forgerock.xacml.core.v3.Functions.IntegerMod");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-abs", "org.forgerock.xacml.core.v3.Functions.IntegerAbs");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-abs", "org.forgerock.xacml.core.v3.Functions.DoubleAbs");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:round", "org.forgerock.xacml.core.v3.Functions.Round");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:floor", "org.forgerock.xacml.core.v3.Functions.Floor");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-normalize-space", "org.forgerock.xacml.core.v3.Functions.StringNormalizeSpace");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-normalize-to-lower-case", "org.forgerock.xacml.core.v3.Functions.StringNormalizeToLowerCase");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-to-integer", "org.forgerock.xacml.core.v3.Functions.DoubleToInteger");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-to-double", "org.forgerock.xacml.core.v3.Functions.IntegerToDouble");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:or", "org.forgerock.xacml.core.v3.Functions.Or");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:and", "org.forgerock.xacml.core.v3.Functions.And");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:n-of", "org.forgerock.xacml.core.v3.Functions.NOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:not", "org.forgerock.xacml.core.v3.Functions.Not");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than", "org.forgerock.xacml.core.v3.Functions.IntegerGreaterThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-greater-than-or-equal", "org.forgerock.xacml.core.v3.Functions.IntegerGreaterThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-less-than", "org.forgerock.xacml.core.v3.Functions.IntegerLessThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-less-than-or-equal", "org.forgerock.xacml.core.v3.Functions.IntegerLessThanOrEqual");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-greater-than", "org.forgerock.xacml.core.v3.Functions.DoubleGreaterThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-greater-than-or-equal", "org.forgerock.xacml.core.v3.Functions.DoubleGreaterThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-less-than", "org.forgerock.xacml.core.v3.Functions.DoubleLessThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-less-than-or-equal", "org.forgerock.xacml.core.v3.Functions.DoubleLessThanOrEqual");

        functions.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-add-dayTimeDuration",
                "org.forgerock.xacml.core.v3.Functions.DatetimeAddDaytimeduration");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-add-yearMonthDuration", "org.forgerock.xacml.core.v3.Functions.DatetimeAddYearmonthduration");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-subtract-dayTimeDuration", "org.forgerock.xacml.core.v3.Functions.DatetimeSubtractDaytimeduration");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-subtractyearMonthDuration", "org.forgerock.xacml.core.v3.Functions.DatetimeSubtractyearmonthduration");

        functions.put("urn:oasis:names:tc:xacml:3.0:function:date-add-yearMonthDuration", "org.forgerock.xacml.core.v3.Functions.DateAddYearmonthduration");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:date-subtract-yearMonthDuration", "org.forgerock.xacml.core.v3.Functions.DateSubtractYearmonthduration");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-greater-than", "org.forgerock.xacml.core.v3.Functions.StringGreaterThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal", "org.forgerock.xacml.core.v3.Functions.StringGreaterThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-less-than", "org.forgerock.xacml.core.v3.Functions.StringLessThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal", "org.forgerock.xacml.core.v3.Functions.StringLessThanOrEqual");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-greater-than", "org.forgerock.xacml.core.v3.Functions.TimeGreaterThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal", "org.forgerock.xacml.core.v3.Functions.TimeGreaterThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-less-than", "org.forgerock.xacml.core.v3.Functions.TimeLessThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal", "org.forgerock.xacml.core.v3.Functions.TimeLessThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:time-in-range", "org.forgerock.xacml.core.v3.Functions.TimeInRange");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than", "org.forgerock.xacml.core.v3.Functions.DatetimeGreaterThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal", "org.forgerock.xacml.core.v3.Functions.DatetimeGreaterThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than", "org.forgerock.xacml.core.v3.Functions.DatetimeLessThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal", "org.forgerock.xacml.core.v3.Functions.DatetimeLessThanOrEqual");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-greater-than", "org.forgerock.xacml.core.v3.Functions.DateGreaterThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal", "org.forgerock.xacml.core.v3.Functions.DateGreaterThanOrEqual");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-less-than", "org.forgerock.xacml.core.v3.Functions.DateLessThan");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal", "org.forgerock.xacml.core.v3.Functions.DateLessThanOrEqual");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-one-and-only", "org.forgerock.xacml.core.v3.Functions.StringOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-bag-size", "org.forgerock.xacml.core.v3.Functions.StringBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-is-in", "org.forgerock.xacml.core.v3.Functions.StringIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-bag", "org.forgerock.xacml.core.v3.Functions.StringBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-one-and-only", "org.forgerock.xacml.core.v3.Functions.BooleanOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-bag-size", "org.forgerock.xacml.core.v3.Functions.BooleanBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-is-in", "org.forgerock.xacml.core.v3.Functions.BooleanIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-bag", "org.forgerock.xacml.core.v3.Functions.BooleanBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-one-and-only", "org.forgerock.xacml.core.v3.Functions.IntegerOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-bag-size", "org.forgerock.xacml.core.v3.Functions.IntegerBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-is-in", "org.forgerock.xacml.core.v3.Functions.IntegerIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-bag", "org.forgerock.xacml.core.v3.Functions.IntegerBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-one-and-only", "org.forgerock.xacml.core.v3.Functions.DoubleOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-bag-size", "org.forgerock.xacml.core.v3.Functions.DoubleBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-is-in", "org.forgerock.xacml.core.v3.Functions.DoubleIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-bag", "org.forgerock.xacml.core.v3.Functions.DoubleBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-one-and-only", "org.forgerock.xacml.core.v3.Functions.TimeOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-bag-size", "org.forgerock.xacml.core.v3.Functions.TimeBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-is-in", "org.forgerock.xacml.core.v3.Functions.TimeIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-bag", "org.forgerock.xacml.core.v3.Functions.TimeBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-one-and-only", "org.forgerock.xacml.core.v3.Functions.DateOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-bag-size", "org.forgerock.xacml.core.v3.Functions.DateBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-is-in", "org.forgerock.xacml.core.v3.Functions.DateIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-bag", "org.forgerock.xacml.core.v3.Functions.DateBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-one-and-only", "org.forgerock.xacml.core.v3.Functions.DatetimeOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-bag-size", "org.forgerock.xacml.core.v3.Functions.DatetimeBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-is-in", "org.forgerock.xacml.core.v3.Functions.DatetimeIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-bag", "org.forgerock.xacml.core.v3.Functions.DatetimeBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-one-and-only", "org.forgerock.xacml.core.v3.Functions.AnyuriOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-bag-size", "org.forgerock.xacml.core.v3.Functions.AnyuriBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-is-in", "org.forgerock.xacml.core.v3.Functions.AnyuriIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-bag", "org.forgerock.xacml.core.v3.Functions.AnyuriBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-one-and-only", "org.forgerock.xacml.core.v3.Functions.HexbinaryOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-bag-size", "org.forgerock.xacml.core.v3.Functions.HexbinaryBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-is-in", "org.forgerock.xacml.core.v3.Functions.HexbinaryIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-bag", "org.forgerock.xacml.core.v3.Functions.HexbinaryBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-one-and-only",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryOneAndOnly");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-bag-size",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryBagSize");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-is-in",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryIsIn");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-bag",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-one-and-only", "org.forgerock.xacml.core.v3.Functions.DaytimedurationOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-bag-size", "org.forgerock.xacml.core.v3.Functions.DaytimedurationBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-is-in", "org.forgerock.xacml.core.v3.Functions.DaytimedurationIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-bag", "org.forgerock.xacml.core.v3.Functions.DaytimedurationBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-one-and-only", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-bag-size", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-is-in", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-bag", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-one-and-only", "org.forgerock.xacml.core.v3.Functions.X500NameOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-bag-size", "org.forgerock.xacml.core.v3.Functions.X500NameBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-is-in", "org.forgerock.xacml.core.v3.Functions.X500NameIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-bag", "org.forgerock.xacml.core.v3.Functions.X500NameBag");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-one-and-only",
                "org.forgerock.xacml.core.v3.Functions.Rfc822NameOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-bag-size",
                "org.forgerock.xacml.core.v3.Functions.Rfc822NameBagSize");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-is-in",
                "org.forgerock.xacml.core.v3.Functions.Rfc822NameIsIn");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-bag",
                "org.forgerock.xacml.core.v3.Functions.Rfc822NameBag");

        functions.put("urn:oasis:names:tc:xacml:2.0:function:string-concatenate", "org.forgerock.xacml.core.v3.Functions.StringConcatenate");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:uri-string-concatenate", "org.forgerock.xacml.core.v3.Functions.UriStringConcatenate");

        functions.put("urn:oasis:names:tc:xacml:3.0:function:any-of", "org.forgerock.xacml.core.v3.Functions.AnyOf");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:all-of", "org.forgerock.xacml.core.v3.Functions.AllOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:any-of-any", "org.forgerock.xacml.core.v3.Functions.AnyOfAny");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:all-of-any", "org.forgerock.xacml.core.v3.Functions.AllOfAny");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:any-of-all", "org.forgerock.xacml.core.v3.Functions.AnyOfAll");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:all-of-all", "org.forgerock.xacml.core.v3.Functions.AllOfAll");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:map", "org.forgerock.xacml.core.v3.Functions.Map");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-match", "org.forgerock.xacml.core.v3.Functions.X500NameMatch");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match", "org.forgerock.xacml.core.v3.Functions.Rfc822NameMatch");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-regexp-match", "org.forgerock.xacml.core.v3.Functions.StringRegexpMatch");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match", "org.forgerock.xacml.core.v3.Functions.AnyuriRegexpMatch");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match", "org.forgerock.xacml.core.v3.Functions.IpaddressRegexpMatch");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match",
                "org.forgerock.xacml.core.v3.Functions.DnsnameRegexpMatch");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:rfc822Name-regexp-match", "org.forgerock.xacml.core.v3.Functions.Rfc822NameRegexpMatch");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match", "org.forgerock.xacml.core.v3.Functions.X500NameRegexpMatch");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-intersection", "org.forgerock.xacml.core.v3.Functions.StringIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.StringAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-union", "org.forgerock.xacml.core.v3.Functions.StringUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-subset", "org.forgerock.xacml.core.v3.Functions.StringSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:string-set-equals", "org.forgerock.xacml.core.v3.Functions.StringSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-intersection", "org.forgerock.xacml.core.v3.Functions.BooleanIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.BooleanAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-union", "org.forgerock.xacml.core.v3.Functions.BooleanUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-subset", "org.forgerock.xacml.core.v3.Functions.BooleanSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:boolean-set-equals", "org.forgerock.xacml.core.v3.Functions.BooleanSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-intersection", "org.forgerock.xacml.core.v3.Functions.IntegerIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.IntegerAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-union", "org.forgerock.xacml.core.v3.Functions.IntegerUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-subset", "org.forgerock.xacml.core.v3.Functions.IntegerSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:integer-set-equals", "org.forgerock.xacml.core.v3.Functions.IntegerSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-intersection", "org.forgerock.xacml.core.v3.Functions.DoubleIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.DoubleAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-union", "org.forgerock.xacml.core.v3.Functions.DoubleUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-subset", "org.forgerock.xacml.core.v3.Functions.DoubleSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:double-set-equals", "org.forgerock.xacml.core.v3.Functions.DoubleSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-intersection", "org.forgerock.xacml.core.v3.Functions.TimeIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.TimeAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-union", "org.forgerock.xacml.core.v3.Functions.TimeUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-subset",
                "org.forgerock.xacml.core.v3.Functions.TimeSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:time-set-equals", "org.forgerock.xacml.core.v3.Functions.TimeSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-intersection", "org.forgerock.xacml.core.v3.Functions.DateIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.DateAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-union", "org.forgerock.xacml.core.v3.Functions.DateUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-subset", "org.forgerock.xacml.core.v3.Functions.DateSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:date-set-equals", "org.forgerock.xacml.core.v3.Functions.DateSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-intersection", "org.forgerock.xacml.core.v3.Functions.DatetimeIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.DatetimeAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-union", "org.forgerock.xacml.core.v3.Functions.DatetimeUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-subset", "org.forgerock.xacml.core.v3.Functions.DatetimeSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dateTime-set-equals", "org.forgerock.xacml.core.v3.Functions.DatetimeSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-intersection", "org.forgerock.xacml.core.v3.Functions.AnyuriIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.AnyuriAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-union", "org.forgerock.xacml.core.v3.Functions.AnyuriUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-subset", "org.forgerock.xacml.core.v3.Functions.AnyuriSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:anyURI-set-equals", "org.forgerock.xacml.core.v3.Functions.AnyuriSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-intersection", "org.forgerock.xacml.core.v3.Functions.HexbinaryIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.HexbinaryAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-union", "org.forgerock.xacml.core.v3.Functions.HexbinaryUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-subset", "org.forgerock.xacml.core.v3.Functions.HexbinarySubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:hexBinary-set-equals", "org.forgerock.xacml.core.v3.Functions.HexbinarySetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-intersection",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-at-least-one-member-of",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-union",
                "org.forgerock.xacml.core.v3.Functions.Base64BinaryUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-subset",
                "org.forgerock.xacml.core.v3.Functions.Base64BinarySubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:base64Binary-set-equals",
                "org.forgerock.xacml.core.v3.Functions.Base64BinarySetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-intersection", "org.forgerock.xacml.core.v3.Functions.DaytimedurationIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.DaytimedurationAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-union", "org.forgerock.xacml.core.v3.Functions.DaytimedurationUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-subset", "org.forgerock.xacml.core.v3.Functions.DaytimedurationSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:dayTimeDuration-set-equals", "org.forgerock.xacml.core.v3.Functions.DaytimedurationSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-intersection", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-union", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-subset", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:yearMonthDuration-set-equals", "org.forgerock.xacml.core.v3.Functions.YearmonthdurationSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-intersection", "org.forgerock.xacml.core.v3.Functions.X500NameIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-at-least-one-member-of", "org.forgerock.xacml.core.v3.Functions.X500NameAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-union", "org.forgerock.xacml.core.v3.Functions.X500NameUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-subset", "org.forgerock.xacml.core.v3.Functions.X500NameSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:x500Name-set-equals", "org.forgerock.xacml.core.v3.Functions.X500NameSetEquals");

        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-intersection", "org.forgerock.xacml.core.v3.Functions.Rfc822NameIntersection");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-at-least-one-member-of",
                "org.forgerock.xacml.core.v3.Functions.Rfc822NameAtLeastOneMemberOf");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-union", "org.forgerock.xacml.core.v3.Functions.Rfc822NameUnion");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-subset", "org.forgerock.xacml.core.v3.Functions.Rfc822NameSubset");
        functions.put("urn:oasis:names:tc:xacml:1.0:function:rfc822Name-set-equals", "org.forgerock.xacml.core.v3.Functions.Rfc822NameSetEquals");


        functions.put("urn:oasis:names:tc:xacml:2.0:function:ipAddress-one-and-only",
                "org.forgerock.xacml.core.v3.Functions.IPAddressOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:ipAddress-bag-size",
                "org.forgerock.xacml.core.v3.Functions.IPAddressBagSize");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:ipAddress-is-in",
                "org.forgerock.xacml.core.v3.Functions.IPAddressIsIn");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:ipAddress-bag",
                "org.forgerock.xacml.core.v3.Functions.IPAddressBag");

        functions.put("urn:oasis:names:tc:xacml:2.0:function:dnsName-one-and-only",
                "org.forgerock.xacml.core.v3.Functions.DNSNameOneAndOnly");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:dnsName-bag-size",
                "org.forgerock.xacml.core.v3.Functions.DNSNameBagSize");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:dnsName-is-in",
                "org.forgerock.xacml.core.v3.Functions.DNSNameIsIn");
        functions.put("urn:oasis:names:tc:xacml:2.0:function:dnsName-bag",
                "org.forgerock.xacml.core.v3.Functions.DNSNameBag");

        /**
         * A.3.9 String functions
         */
        functions.put("urn:oasis:names:tc:xacml:2.0:function:string-concatenate", "org.forgerock.xacml.core.v3.Functions.StringConcatenate");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:boolean-from-string", "org.forgerock.xacml.core.v3.Functions.BooleanFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-boolean", "org.forgerock.xacml.core.v3.Functions.StringFromBoolean");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:integer-from-string", "org.forgerock.xacml.core.v3.Functions.IntegerFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-integer", "org.forgerock.xacml.core.v3.Functions.StringFromInteger");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:double-from-string", "org.forgerock.xacml.core.v3.Functions.DoubleFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-double", "org.forgerock.xacml.core.v3.Functions.StringFromDouble");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:time-from-string", "org.forgerock.xacml.core.v3.Functions.TimeFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-time", "org.forgerock.xacml.core.v3.Functions.StringFromTime");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:date-from-string", "org.forgerock.xacml.core.v3.Functions.DateFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-date", "org.forgerock.xacml.core.v3.Functions.StringFromDate");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dateTime-from-string", "org.forgerock.xacml.core.v3.Functions.DatetimeFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-dateTime", "org.forgerock.xacml.core.v3.Functions.StringFromDatetime");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:anyURI-from-string", "org.forgerock.xacml.core.v3.Functions.AnyuriFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-anyURI", "org.forgerock.xacml.core.v3.Functions.StringFromAnyURI");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-from-string", "org.forgerock.xacml.core.v3.Functions.DayTimeDurationFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-dayTimeDuration", "org.forgerock.xacml.core.v3.Functions.StringFromDayTimeDuration");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-from-string", "org.forgerock.xacml.core.v3.Functions.YearMonthDurationFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-yearMonthDuration", "org.forgerock.xacml.core.v3.Functions.StringFromYearMonthDuration");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:x500Name-from-string", "org.forgerock.xacml.core.v3.Functions.X500NameFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-x500Name", "org.forgerock.xacml.core.v3.Functions.StringFromX500Name");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:rfc822Name-from-string", "org.forgerock.xacml.core.v3.Functions.Rfc822NameFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-rfc822Name", "org.forgerock.xacml.core.v3.Functions.StringFromRfc822Name");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:ipAddress-from-string", "org.forgerock.xacml.core.v3.Functions.IPAddressFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-ipAddress", "org.forgerock.xacml.core.v3.Functions.StringFromIPAddress");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:dnsName-from-string", "org.forgerock.xacml.core.v3.Functions.DNSNameFromString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-from-dnsName", "org.forgerock.xacml.core.v3.Functions.StringFromDNSName");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-starts-with", "org.forgerock.xacml.core.v3.Functions.StringStartswith");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:anyURI-starts-with", "org.forgerock.xacml.core.v3.Functions.AnyUriStartswith");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-ends-with", "org.forgerock.xacml.core.v3.Functions.StringEndswith");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:anyURI-ends-with", "org.forgerock.xacml.core.v3.Functions.AnyUriEndswith");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-contains", "org.forgerock.xacml.core.v3.Functions.StringContains");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:anyURI-contains", "org.forgerock.xacml.core.v3.Functions.AnyUriContains");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:string-substring", "org.forgerock.xacml.core.v3.Functions.StringSubString");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:anyURI-substring", "org.forgerock.xacml.core.v3.Functions.AnyUriSubString");

        /**
         * Specialized Functions
         */
        functions.put("urn:oasis:names:tc:xacml:3.0:function:access-permitted",
                "org.forgerock.xacml.core.v3.Functions.AccessPermitted");

        /**
         * These XPath Functions have not been implemented, as they have been Deprecated per the specification.
         */
        functions.put("urn:oasis:names:tc:xacml:3.0:function:xpath-node-count", "org.forgerock.xacml.core.v3.Functions.XpathNodeCount");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:xpath-node-equal", "org.forgerock.xacml.core.v3.Functions.XpathNodeEqual");
        functions.put("urn:oasis:names:tc:xacml:3.0:function:xpath-node-match", "org.forgerock.xacml.core.v3.Functions.XpathNodeMatch");

        /**
         * ForgeRock Specific Functions
         */
        functions.put("urn:oasis:names:forgerock:xacml:1.0:function:VariableDereference", "org.forgerock.xacml.core.v3.Functions.VariableDereference");
        functions.put("urn:forgerock:xacml:1.0:function:MatchAnyOf", "org.forgerock.xacml.core.v3.Functions.MatchAnyOf");
        functions.put("urn:forgerock:xacml:1.0:function:MatchAllOf", "org.forgerock.xacml.core.v3.Functions.MatchAllOf");

    }
}
