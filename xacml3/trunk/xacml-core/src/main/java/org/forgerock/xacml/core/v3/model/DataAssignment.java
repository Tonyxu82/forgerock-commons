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



/*
    This class Encapsulates a DataDesignator from the XACML policy.
    In this case, we have To Fetch the data from PIP

 */


import com.sun.identity.entitlement.xacml3.core.*;
import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACML3PrivilegeUtils;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.json.JSONException;
import org.json.JSONObject;

public class DataAssignment extends FunctionArgument {
    private String category;
    private String attributeID;
    private FunctionArgument expression;
    private boolean presence;

    public DataAssignment() {
    }
    public DataAssignment(AttributeAssignmentExpression att) {
        this.category = att.getCategory();
        this.attributeID = att.getAttributeId();
        this.expression = XACML3PrivilegeUtils.getAssignmentFunction(att);
    }

    public  FunctionArgument evaluate(XACMLEvalContext pip) throws XACML3EntitlementException {
        throw new NotApplicableException("Evaluate called for DataAssignment");
    };
    public  Object getValue(XACMLEvalContext pip) throws XACML3EntitlementException {
        throw new NotApplicableException("Evaluate called for DataAssignment");
    };

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = super.toJSONObject();
        jo.put("category",category);
        jo.put("attributeID",attributeID);
        jo.put("expression",expression.toJSONObject());
        return jo;
    }
    protected void init(JSONObject jo) throws JSONException {
        super.init(jo);
        this.category = jo.optString("category");
        this.attributeID = jo.optString("attributeID");
        this.expression = FunctionArgument.getInstance(jo.getJSONObject("expression")) ;
        return;
    };

    public AttributeAssignmentExpression getXACML(XACMLEvalContext pip) {
        ObjectFactory objectFactory = new ObjectFactory();
        AttributeAssignmentExpression ret = new AttributeAssignmentExpression();
        ret.setCategory(category);
        ret.setAttributeId(attributeID);
        AttributeValue av = new AttributeValue();
        try {
        av.getContent().add(expression.getValue(pip));
        } catch (XACML3EntitlementException ex) {
            av.getContent().add("null");
        }
        ret.setExpression(objectFactory.createAttributeValue(av));
        return ret;
    }


    public String toXML(String type) {
        /*
             Handle Match AnyOf and AllOf specially
        */
        String retVal = "<AttributeAssignmentExpression AttributeId=\"" + attributeID + "\" >";
        retVal = retVal + expression.toXML("Allow");
        retVal = retVal + "</AttributeAssignmentExpression>";

        return retVal;
    }


}
