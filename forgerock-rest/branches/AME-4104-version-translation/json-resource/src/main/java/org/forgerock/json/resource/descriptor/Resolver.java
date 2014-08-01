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
 * Copyright 2013 ForgeRock AS.
 */
package org.forgerock.json.resource.descriptor;

import java.io.Closeable;
import java.util.Collection;

import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ResultHandler;

public interface Resolver extends Closeable {

    RequestHandler getRequestHandler(RelationDescriptor relation) throws ResourceException;

    void getRelationsForResource(RelationDescriptor relation, String resourceId,
            ResultHandler<Collection<RelationDescriptor>> handler);

    void close();
}
