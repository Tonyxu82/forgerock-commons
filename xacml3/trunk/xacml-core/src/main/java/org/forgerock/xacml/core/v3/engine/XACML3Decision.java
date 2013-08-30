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

import org.forgerock.xacml.core.v3.interfaces.Entitlement;
import com.sun.identity.entitlement.xacml3.core.*;

import java.util.HashSet;
import java.util.Set;

/* A decision encapsulates a
    Decision String,
    Advices,
    and Obligations
*/

public class XACML3Decision {

    private DecisionType trueDecision;
    private DecisionType falseDecision;
    private DecisionType decision;

    private Status status;
    private ObligationExpressions obligations;
    private AdviceExpressions advices;

    private String ruleID;
    private String resourceID;

    public static final String OK = "urn:oasis:names:tc:xacml:1.0:status:ok";
    public static final String MISSING_ATTRIBUTE = "urn:oasis:names:tc:xacml:1.0:status:missing-attribute";
    public static final String SYNTAX_ERROR = "urn:oasis:names:tc:xacml:1.0:status:syntax-error";
    public static final String PROECESSING_ERROR = "urn:oasis:names:tc:xacml:1.0:status:processing-error";

    public  XACML3Decision() {
    }

    public  XACML3Decision(String ruleid, String resid, String effect) {
        ruleID = ruleid;
        resourceID = resid;
        if (effect.equals("Permit")) {
            trueDecision = DecisionType.fromValue("Permit");
            falseDecision = DecisionType.fromValue("Deny");
        } else {
            trueDecision = DecisionType.fromValue("Deny");
            falseDecision = DecisionType.fromValue("Permit");
        }
        setStatus(OK);
    }

    public Status getStatus() {
        if (status == null) {
             setStatus(SYNTAX_ERROR);
        }
        return status;
    }
    public void setStatus(String stat) {
        status = new Status();
        StatusCode sc = new StatusCode();
        sc.setValue(stat);
    }

    public DecisionType getDecision() {
        if (decision == null) {
            setDecision("Indeterminate");
        }
        return decision;
    }
    public void setDecision(String dec) {
        decision = DecisionType.fromValue(dec);
    }

    public void setEffect(boolean result) {
        if (result) {
             decision = trueDecision;
        } else {
            decision = falseDecision;
        }
    }

    public ObligationExpressions getObligations() {
        return obligations;
    }
    public void setObligations(ObligationExpressions ob) {
        obligations = ob;
    }
    public AdviceExpressions getAdvices() {
        return advices;
    }
    public void setAdvices(AdviceExpressions ad) {
        advices = ad;
    }
    public void setRuleID(String ruleid) {
            ruleID = ruleid;
    }
    public void setResID(String resid) {
           resourceID = resid;
    }
    public String getRuleID() {
         return ruleID;
    }
    public String getResID() {
         return resourceID;
    }

    public Entitlement asEntitlement() {
        Set<String> actions = new HashSet<String>();
        actions.add(decision.value());
        XACML3Entitlement ent = new XACML3Entitlement(ruleID,resourceID,actions);
        ent.setRuleName(ruleID);
        ent.setDecision(this);
        return ent;
    }

}
