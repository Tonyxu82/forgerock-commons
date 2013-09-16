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

package org.forgerock.xacml.core.v3.engine;

import com.sun.identity.entitlement.xacml3.core.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;
import javax.xml.bind.JAXBElement;

import com.sun.identity.entitlement.xacml3.core.AllOf;
import com.sun.identity.entitlement.xacml3.core.AnyOf;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.forgerock.xacml.core.v3.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class with utility methods to map from
 * </code>com.sun.identity.entitlement.xacml3.core.Policy</code>
 *
 * @author Allan.Foster@forgerock.com
 * X500Name
 *
 */
public class XACML3PrivilegeUtils {

    // Global Pattern Definitions
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECONDS = "yyyy-MM-dd:HH:mm:ss.SSS";
    public static final String YEAR_MONTH_DAY =  "yyyy-MM-dd";
    public static final String YEAR_MONTH = "yyyy-MM";
    public static final String DAY_HOUR_MINUTE_SECOND_MILLISECONDS = "ddd:HH:mm:ss.SSS";
    public static final String HOUR_MINUTE_SECOND_MILLISECONDS = "HH:mm:ss.SSS";
    public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

    // Static Methods

    static FunctionArgument getTargetFunction(Target target, Set<String> rSelectors) {
        List<AnyOf> anyOfList = target.getAnyOf();

        XACMLFunction retVal = XACMLFunction.getInstance("urn:forgerock:xacml:1.0:function:MatchAnyOf");

        for (AnyOf anyOf : anyOfList) {
            List<AllOf> allOfList = anyOf.getAllOf();
            for (AllOf allOf : allOfList) {
                XACMLFunction parent = XACMLFunction.getInstance("urn:forgerock:xacml:1.0:function:MatchAllOf");
                List<Match> matchList = allOf.getMatch();
                for (Match match : matchList) {
                    String mName = match.getMatchId();
                    AttributeValue attr = match.getAttributeValue();
                    DataValue dv = new DataValue(attr.getDataType(),(String)(attr.getContent().get(0)));

                    AttributeDesignator attrd = match.getAttributeDesignator();
                    if (attrd == null) { continue; };
                    DataDesignator dd = new DataDesignator(attrd.getDataType(),attrd.getCategory(),attrd.getAttributeId(),attrd.isMustBePresent());
                    if (attrd.getCategory().contains(":resource")) {
                        rSelectors.add(attrd.getAttributeId());
                    }
                    // XACMLFunction.getInstance("urn:oasis:names:tc:xacml:1.0:function:any-of").XACMLFunction.getInstance(mName),
                    parent.addArgument(XACMLFunction.getInstance("urn:oasis:names:tc:xacml:3.0:function:any-of")
                            .addArgument(XACMLFunction.getInstance(mName))
                            .addArgument(dv)
                            .addArgument(dd));

                }
            retVal.addArgument(parent);
            }
        }
        if (retVal.getArgCount() == 0) {
            retVal.addArgument(new DataValue(DataType.XACMLBOOLEAN,"true"));
        }
        return retVal;
    }

    public static Map<String,FunctionArgument> getVariableDefinitions(Policy policy) {
        if (policy == null) {
            return null;
        }
        Map<String,FunctionArgument> varsMap = new HashMap<String,FunctionArgument>();
        List<Object> obList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        for (Object ob : obList) {
            if (ob instanceof VariableDefinition) {
                VariableDefinition it = (VariableDefinition)ob;
                try {
                    varsMap.put(it.getVariableId(), XACML3PrivilegeUtils.getFunction((it.getExpression())));
                } catch (XACML3EntitlementException ex) {
                      /* Just dont add it,  it was NULL */
                }
            }
        }
        return varsMap;
    }

    public static List<XACML3PolicyRule> getRules(Policy policy,XACML3Policy parent) {
        if (policy == null) {
            return null;
        }
        List<XACML3PolicyRule> ruleList = new ArrayList<XACML3PolicyRule>();
        List<Object> obList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        for (Object ob : obList) {
            if (ob instanceof Rule) {
                ruleList.add(new XACML3PolicyRule((Rule)ob,parent));
            }
        }
        return ruleList;
    }


    /*
    *     {@link JAXBElement }{@code <}{@link AttributeValue }{@code >}
    *     {@link JAXBElement }{@code <}{@link AttributeDesignator }{@code >}
    *     {@link JAXBElement }{@code <}{@link Function }{@code >}
    *     {@link JAXBElement }{@code <}{@link AttributeSelector }{@code >}
    *     {@link JAXBElement }{@code <}{@link Expression }{@code >}
    *     {@link JAXBElement }{@code <}{@link Apply }{@code >}
    *     {@link JAXBElement }{@code <}{@link VariableReference }{@code >}


    */
    static private  FunctionArgument getFunction(JAXBElement je) throws XACML3EntitlementException {
        FunctionArgument retVal = null;
        Class clazz = je.getDeclaredType();

        if (clazz.equals(Apply.class)) {
            Apply apply = (Apply)je.getValue();
            String functionId = apply.getFunctionId();
            XACMLFunction it = XACMLFunction.getInstance(functionId);

            List<JAXBElement<?>> expressionList = apply.getExpression();
            for (JAXBElement jaxe : expressionList) {
                it.addArgument(getFunction(jaxe));
            }
            retVal = it;

        } else if (clazz.equals(AttributeValue.class)) {

            AttributeValue attr = (AttributeValue)je.getValue();
            DataValue dv = new DataValue(attr.getDataType(),(String)(attr.getContent().get(0)));
            retVal = dv;

        } else if (clazz.equals(AttributeDesignator.class)) {

            AttributeDesignator attr = (AttributeDesignator)je.getValue();
            DataDesignator dd = new DataDesignator(attr.getDataType(),attr.getCategory(),attr.getAttributeId(),attr.isMustBePresent());
            retVal = dd;

        } else if (clazz.equals(Function.class)) {

        } else if (clazz.equals(AttributeSelector.class)) {

        } else if (clazz.equals(VariableReference.class)) {
            VariableReference vr = (VariableReference)je.getValue();
            XACMLFunction it = XACMLFunction.getInstance("urn:oasis:names:forgerock:xacml:1.0:function:VariableDereference");
            if (it == null) {
                throw new XACML3EntitlementException("Null Variable Reference");
            }
            it.addArgument(new DataValue(DataType.XACMLSTRING,(String)(vr.getVariableId())));
            retVal = it;
        }
         return retVal;
    }


    public static FunctionArgument getConditionFunction(Condition cond) {

        try {
        return (getFunction(cond.getExpression()));
        } catch (XACML3EntitlementException ex) {
            return null;
        }
    }
    public static FunctionArgument getAssignmentFunction(AttributeAssignmentExpression assign) {
        try {
        return (getFunction(assign.getExpression()));
        } catch (XACML3EntitlementException ex) {
            return null;
        }
    }

    public static Date stringToDate(String dateString) {

        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_DAY);
        sdf.setTimeZone(GMT_TIMEZONE);
        Date retVal = new Date();
        try {
            retVal = sdf.parse(dateString);
        } catch (java.text.ParseException pe) {
            //TODO: log debug warning
        }
        return retVal;

    }

    public static Date stringToDateTime(String dateTimeString) {

        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECONDS);
        sdf.setTimeZone(GMT_TIMEZONE);
        Date retVal = new Date();
        try {
            retVal = sdf.parse(dateTimeString);
        } catch (java.text.ParseException pe) {
            //TODO: log debug warning
        }
        return retVal;

    }

    public static Long  stringDayTimeDurationToLongDuration(String dayTimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_HOUR_MINUTE_SECOND_MILLISECONDS);
        sdf.setTimeZone(GMT_TIMEZONE);
        Long retVal = new Long(0);
        try {
            Date date = sdf.parse(dayTimeString);
            retVal = date.getTime();
        } catch (java.text.ParseException pe) {
            // TODO: log debug warning
        }
        return retVal;
    }

    public static String  dayTimeDurationToString(Long dayTimeDuration) {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_HOUR_MINUTE_SECOND_MILLISECONDS);
        sdf.setTimeZone(GMT_TIMEZONE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dayTimeDuration);
        return sdf.format(calendar.getTime());
    }

    public static Date stringToTime(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(HOUR_MINUTE_SECOND_MILLISECONDS);
        sdf.setTimeZone(GMT_TIMEZONE);
        Date retVal = new Date();
        try {
            retVal = sdf.parse(dateString);
        } catch (java.text.ParseException pe) {
            // TODO: log debug warning
        }
        return retVal;

    }

    public static String timeToString(Date date){
        SimpleDateFormat sdf1 = new SimpleDateFormat(HOUR_MINUTE_SECOND_MILLISECONDS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if ( (calendar.getTimeZone() == null) ) {
            calendar.setTimeZone(GMT_TIMEZONE);
        }
        sdf1.setTimeZone(TimeZone.getDefault());
        return sdf1.format(date);
    }

    public static String dateToString(Date date){
        SimpleDateFormat sdf1 = new SimpleDateFormat(YEAR_MONTH_DAY);
        sdf1.setTimeZone(GMT_TIMEZONE);
        return sdf1.format(date);
    }

    public static String dateTimeToString(Date date, char separator){
        if (separator == 0) {
            separator = 'T';
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(YEAR_MONTH_DAY);
        SimpleDateFormat sdf2 = new SimpleDateFormat(HOUR_MINUTE_SECOND_MILLISECONDS);
        sdf1.setTimeZone(GMT_TIMEZONE);
        sdf2.setTimeZone(GMT_TIMEZONE);
        return sdf1.format(date) + separator + sdf2.format(date);
    }

    public static Date stringToDate(String dateString, final String pattern) {
        Date retVal = new Date();
        if ( (dateString == null) || (pattern == null) ) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(GMT_TIMEZONE);
        try {
            retVal = simpleDateFormat.parse(dateString);
        } catch (java.text.ParseException pe) {
            //TODO: log debug warning
        }
        return retVal;
    }

    public static byte[] convertHexBinaryStringToByteArray(final String hexBinaryString) {
        try {
            if (hexBinaryString == null) {
                return new byte[0];
            }
            return Hex.decodeHex(hexBinaryString.toCharArray());
        } catch(DecoderException de) {
            // TODO Handle
            return new byte[0];
        }
    }

    public static byte[] convertBase64StringToByteArray(final String base64String) {
            if (base64String == null) {
                return new byte[0];
            }
            Base64 base64 = new Base64();
            return base64.decode(base64String);
    }

    public static Request parseJSON(String req) {
        Request request = new Request();
        try {
            JSONObject jo = new JSONObject(req);

            request.setReturnPolicyIdList(jo.optBoolean("ReturnPolicyIdList"));
            request.setCombinedDecision(jo.optBoolean("CombinedDecision"));
            // request.setMultiRequests(jo.opt("MultiRequests"));
            // request.setRequestDefaults(jo.opt("RequestDefaults"));

            List<Attributes> attributes = request.getAttributes();

            JSONArray array = jo.optJSONArray("Attributes");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    Attributes as = new Attributes();
                    JSONObject entry = (JSONObject)array.get(i);
                    as.setCategory(entry.getString("Category"));
                    JSONArray json = (JSONArray)entry.getJSONArray("Attribute");
                    if (json != null) {
                        for (int j = 0;j < json.length(); j++) {
                            JSONObject att = (JSONObject)json.get(j);
                            Attribute a = new Attribute();
                            a.setAttributeId(att.optString("AttributeId"));
                            a.setIncludeInResult(att.optBoolean("IncludeInResult"));
                            a.setIssuer(att.optString("Issuer"));
                            List<AttributeValue> av = a.getAttributeValue();
                            AttributeValue aValue = new AttributeValue();
                            List<Object> content = aValue.getContent();
                            JSONObject val = att.optJSONObject("AttributeValue");
                            content.add(val.opt("$"));
                            av.add(aValue);
                            as.getAttribute().add(a);
                        }
                    }
                 attributes.add(as);
                }
            }
        } catch (Exception ex ) {

        }
        return request;
    }

}
