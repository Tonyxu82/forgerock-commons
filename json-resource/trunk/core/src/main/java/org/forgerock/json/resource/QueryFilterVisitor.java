/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 */

package org.forgerock.json.resource;

import java.util.List;

import org.forgerock.json.fluent.JsonPointer;

/**
 * A visitor of {@code QueryFilter}s, in the style of the visitor design
 * pattern.
 * <p>
 * Classes implementing this interface can query filters in a type-safe manner.
 * When a visitor is passed to a filter's accept method, the corresponding visit
 * method most applicable to that filter is invoked.
 *
 * @param <R>
 *            The return type of this visitor's methods. Use
 *            {@link java.lang.Void} for visitors that do not need to return
 *            results.
 * @param <P>
 *            The type of the additional parameter to this visitor's methods.
 *            Use {@link java.lang.Void} for visitors that do not need an
 *            additional parameter.
 */
public interface QueryFilterVisitor<R, P> {

    /**
     * Visits a boolean literal filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param value
     *            The boolean literal value.
     * @return Returns a visitor specified result.
     */
    R visitBooleanLiteralFilter(P p, boolean value);

    /**
     * Visits an {@code and} filter.
     * <p>
     * <b>Implementation note</b>: for the purposes of matching, an empty
     * sub-filter list should always evaluate to {@code true}.
     *
     * @param p
     *            A visitor specified parameter.
     * @param subFilters
     *            The unmodifiable list of sub-filters.
     * @return Returns a visitor specified result.
     */
    R visitAndFilter(P p, List<QueryFilter> subFilters);

    /**
     * Visits a {@code not} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param subFilter
     *            The sub-filter.
     * @return Returns a visitor specified result.
     */
    R visitNotFilter(P p, QueryFilter subFilter);

    /**
     * Visits an {@code or} filter.
     * <p>
     * <b>Implementation note</b>: for the purposes of matching, an empty
     * sub-filter list should always evaluate to {@code false}.
     *
     * @param p
     *            A visitor specified parameter.
     * @param subFilters
     *            The unmodifiable list of sub-filters.
     * @return Returns a visitor specified result.
     */
    R visitOrFilter(P p, List<QueryFilter> subFilters);

    /**
     * Visits a {@code present} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @return Returns a visitor specified result.
     */
    R visitPresentFilter(P p, JsonPointer field);

    /**
     * Visits a {@code greater than} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @param valueAssertion
     *            The value assertion.
     * @return Returns a visitor specified result.
     */
    R visitGreaterThanFilter(P p, JsonPointer field, Object valueAssertion);

    /**
     * Visits a {@code greater than or equal to} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @param valueAssertion
     *            The value assertion.
     * @return Returns a visitor specified result.
     */
    R visitGreaterThanOrEqualToFilter(P p, JsonPointer field, Object valueAssertion);

    /**
     * Visits a {@code less than} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @param valueAssertion
     *            The value assertion.
     * @return Returns a visitor specified result.
     */
    R visitLessThanFilter(P p, JsonPointer field, Object valueAssertion);

    /**
     * Visits a {@code less than or equal to} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @param valueAssertion
     *            The value assertion.
     * @return Returns a visitor specified result.
     */
    R visitLessThanOrEqualToFilter(P p, JsonPointer field, Object valueAssertion);

    /**
     * Visits a {@code equality} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @param valueAssertion
     *            The value assertion.
     * @return Returns a visitor specified result.
     */
    R visitEqualsFilter(P p, JsonPointer field, Object valueAssertion);

    /**
     * Visits a {@code comparison} filter.
     *
     * @param p
     *            A visitor specified parameter.
     * @param field
     *            A pointer to the field within JSON resource to be compared.
     * @param matchingRuleId
     *            The ID of the matching rule to be used for performing the
     *            comparison.
     * @param valueAssertion
     *            The value assertion.
     * @return Returns a visitor specified result.
     */
    R visitExtendedMatchFilter(P p, JsonPointer field, String matchingRuleId, Object valueAssertion);

}
