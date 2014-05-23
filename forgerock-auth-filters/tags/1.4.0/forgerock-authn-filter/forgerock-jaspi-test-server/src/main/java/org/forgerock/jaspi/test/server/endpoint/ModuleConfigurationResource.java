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
 * Copyright 2013-2014 ForgeRock AS.
 */

package org.forgerock.jaspi.test.server.endpoint;

import org.forgerock.jaspi.runtime.context.config.ModuleConfigurationFactory;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.Resource;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.ServerContext;
import org.forgerock.json.resource.SingletonResourceProvider;
import org.forgerock.json.resource.UpdateRequest;

import java.util.HashMap;

public enum ModuleConfigurationResource implements SingletonResourceProvider, ModuleConfigurationFactory {

    INSTANCE;

    private JsonValue moduleConfiguration =
            JsonValue.json(
                JsonValue.object(
                    JsonValue.field(ModuleConfigurationFactory.SERVER_AUTH_CONTEXT_KEY, JsonValue.object(
                        JsonValue.field(ModuleConfigurationFactory.AUTH_MODULES_KEY, JsonValue.array())
                    ))
                )
            );

    public static ModuleConfigurationFactory getModuleConfigurationFactory() {
        return ModuleConfigurationResource.INSTANCE;
    }

    @Override
    public JsonValue getConfiguration() {//TODO define exception that can be thrown???
        return moduleConfiguration;
    }

    @Override
    public void readInstance(ServerContext context, ReadRequest request, ResultHandler<Resource> handler) {
        handler.handleResult(new Resource("ModuleConfiguration", "0", moduleConfiguration));
    }

    @Override
    public void updateInstance(ServerContext context, UpdateRequest request, ResultHandler<Resource> handler) {
        moduleConfiguration = request.getContent();
        handler.handleResult(new Resource("ModuleConfiguration", "0", moduleConfiguration));
    }

    @Override
    public void actionInstance(ServerContext context, ActionRequest request, ResultHandler<JsonValue> handler) {
        handler.handleError(ResourceException.getException(ResourceException.NOT_SUPPORTED));
    }

    @Override
    public void patchInstance(ServerContext context, PatchRequest request, ResultHandler<Resource> handler) {
        handler.handleError(ResourceException.getException(ResourceException.NOT_SUPPORTED));
    }
}
