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
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2012-2014 ForgeRock AS.
 */
package org.forgerock.json.resource;

import static org.forgerock.util.Reject.checkNotNull;

import java.util.Collections;
import java.util.Map;

import org.forgerock.json.fluent.JsonValue;

/**
 * A {@link ServerContext} which is created when a request has been routed. The
 * context includes:
 * <ul>
 * <li>the portion of the request URI which matched the URI template
 * <li>a method for obtaining the base URI, which represents the portion of the
 * request URI which has been routed so far. This is obtained dynamically by
 * concatenating the matched URI with matched URIs in parent router contexts
 * <li>a map which contains the parsed URI template variables, keyed on the URI
 * template variable name.
 * </ul>
 * <p>
 * When a request is routed the request is duplicated and the
 * {@link Request#getResourceName() resource name} trimmed so that it is
 * relative to the current resource being accessed. More specifically, the
 * "relativized" resource name represents the portion of the resource URI which
 * follows the portion matched during routing by the URI template or "" (empty
 * string) if the URI template matched exactly. The matched portion of the
 * resource URI and the base URI may be accessed via this router context.
 * Example:
 *
 * <pre>
 * Router top = new Router();
 * Router users = new Router();
 * Router devices = new Router();
 * top.addRoute(RoutingMode.STARTS_WITH, &quot;users/{userId}&quot;, users);
 * users.addRouter(RoutingMode.STARTS_WITH, &quot;devices/{deviceId}&quot;, devices);
 * </pre>
 *
 * A request against "users/1" will be routed to the users router with a base
 * URI of "users/1", a matched URI of "users/1", and a resource name of ""
 * (empty string).
 * <p>
 * A request against "users/1/devices/0" will be routed to the users router with
 * a base URI of "users/1", a matched URI of "users/1", and a resource name of
 * "devices/0". It will then be routed to the devices router with a base URI of
 * "users/1/devices/0", a matched URI of "devices/0", and a resource name of ""
 * (empty string).
 * <p>
 * Here is an example of the JSON representation of a routing context:
 *
 * <pre>
 * {
 *   "id"     : "56f0fb7e-3837-464d-b9ec-9d3b6af665c3",
 *   "class"  : "org.forgerock.json.resource.provider.RouterContext",
 *   "parent" : {
 *       ...
 *   },
 *   "matchedUri" : "users/bjensen",
 *   "uriTemplateVariables" : {
 *       "userId" : "bjensen",
 *       "deviceId" : "0"
 *   }
 * }
 * </pre>
 *
 * @see Router
 */
public final class RouterContext extends ServerContext {

    /** the client-friendly name of this context. */
    private static final String CONTEXT_NAME = "router";

    // Persisted attribute names.
    private static final String ATTR_MATCHED_URI = "matchedUri";
    private static final String ATTR_URI_TEMPLATE_VARIABLES = "uriTemplateVariables";

    private final Map<String, String> uriTemplateVariables;

    /**
     * Restore from JSON representation.
     *
     * @param savedContext
     *            The JSON representation from which this context's attributes
     *            should be parsed.
     * @param config
     *            The persistence configuration.
     * @throws ResourceException
     *             If the JSON representation could not be parsed.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    RouterContext(final JsonValue savedContext, final PersistenceConfig config)
            throws ResourceException {
        super(savedContext, config);
        this.uriTemplateVariables =
                Collections.unmodifiableMap((Map) data.get(ATTR_URI_TEMPLATE_VARIABLES).required()
                        .asMap());
    }

    /**
     * Creates a new routing context having the provided parent, URI template
     * variables, and an ID automatically generated using
     * {@code UUID.randomUUID()}.
     *
     * @param parent
     *            The parent server context.
     * @param uriTemplateVariables
     *            A {@code Map} containing the parsed URI template variables,
     *            keyed on the URI template variable name.
     */
    RouterContext(final ServerContext parent, final String matchedUri,
            final Map<String, String> uriTemplateVariables) {
        super(checkNotNull(parent, "Cannot instantiate RouterContext with null parent Context"));
        data.put(ATTR_MATCHED_URI, matchedUri);
        this.uriTemplateVariables = Collections.unmodifiableMap(uriTemplateVariables);
        data.put(ATTR_URI_TEMPLATE_VARIABLES, this.uriTemplateVariables);
    }

    /**
     * Get this Context's name.
     *
     * @return this object's name
     */
    public String getContextName() {
        return CONTEXT_NAME;
    }

    /**
     * Returns the portion of the request URI which has been routed so far. This
     * is obtained dynamically by concatenating the matched URI with the base
     * URI of the parent router context if present. The base URI is never
     * {@code null} but may be "" (empty string).
     *
     * @return The non-{@code null} portion of the request URI which has been
     *         routed so far.
     */
    public String getBaseUri() {
        final StringBuilder builder = new StringBuilder();
        final Context parent = getParent();
        if (parent.containsContext(RouterContext.class)) {
            final String baseUri = parent.asContext(RouterContext.class).getBaseUri();
            if (baseUri.length() > 1) {
                builder.append(baseUri);
            }
        }
        final String matchedUri = getMatchedUri();
        if (matchedUri.length() > 0) {
            if (builder.length() > 0) {
                builder.append('/');
            }
            builder.append(matchedUri);
        }
        return builder.toString();
    }

    /**
     * Returns the portion of the request URI which matched the URI template.
     * The matched URI is never {@code null} but may be "" (empty string).
     *
     * @return The non-{@code null} portion of the request URI which matched the
     *         URI template.
     */
    public String getMatchedUri() {
        return data.get(ATTR_MATCHED_URI).asString();
    }

    /**
     * Returns an unmodifiable {@code Map} containing the parsed URI template
     * variables, keyed on the URI template variable name.
     *
     * @return The unmodifiable {@code Map} containing the parsed URI template
     *         variables, keyed on the URI template variable name.
     */
    public Map<String, String> getUriTemplateVariables() {
        return uriTemplateVariables;
    }
}
