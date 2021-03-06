/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2012 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.resource;

/**
 * The final result of a query request returned after all resources matching the
 * request have been returned. In addition to indicating that no more resources
 * are to be returned by the query, the query result will contain page results
 * state information if result paging has been enabled for the query.
 */
public final class QueryResult {

    /**
     * The name of the field which contains the error in the JSON
     * representation.
     */
    public static final String FIELD_ERROR = "error";
    /**
     * The name of the field which contains the paged results cookie in the JSON
     * representation.
     */
    public static final String FIELD_PAGED_RESULTS_COOKIE = QueryRequest.FIELD_PAGED_RESULTS_COOKIE;

    /**
     * The name of the field which contains the remaining paged results in the
     * JSON representation.
     */
    public static final String FIELD_REMAINING_PAGED_RESULTS = "remainingPagedResults";

    /**
     * The name of the field which contains the result count in the JSON
     * representation.
     */
    public static final String FIELD_RESULT_COUNT = "resultCount";

    /**
     * The name of the field which contains the array of matching resources in
     * the JSON representation.
     */
    public static final String FIELD_RESULT = "result";

    private final String pagedResultsCookie;
    private final int remainingPagedResults;

    /**
     * Creates a new query result with a {@code null} paged results cookie and
     * no estimate of the total number of remaining results.
     */
    public QueryResult() {
        this(null, -1);
    }

    /**
     * Creates a new query result with the provided paged results cookie and
     * estimate of the total number of remaining results.
     *
     * @param pagedResultsCookie
     *            The opaque cookie which should be used with the next paged
     *            results query request, or {@code null} if paged results were
     *            not requested, or if there are not more pages to be returned.
     * @param remainingPagedResults
     *            An estimate of the total number of remaining results to be
     *            returned in subsequent paged results query requests, or
     *            {@code -1} if paged results were not requested, or if the
     *            total number of remaining results is unknown.
     */
    public QueryResult(final String pagedResultsCookie, final int remainingPagedResults) {
        this.pagedResultsCookie = pagedResultsCookie;
        this.remainingPagedResults = remainingPagedResults;
    }

    /**
     * Returns the opaque cookie which should be used with the next paged
     * results query request.
     *
     * @return The opaque cookie which should be used with the next paged
     *         results query request, or {@code null} if paged results were not
     *         requested, or if there are not more pages to be returned.
     */
    public String getPagedResultsCookie() {
        return pagedResultsCookie;
    }

    /**
     * Returns an estimate of the total number of remaining results to be
     * returned in subsequent paged results query requests.
     *
     * @return An estimate of the total number of remaining results to be
     *         returned in subsequent paged results query requests, or
     *         {@code -1} if paged results were not requested, or if the total
     *         number of remaining results is unknown.
     */
    public int getRemainingPagedResults() {
        return remainingPagedResults;
    }

}
