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

package org.forgerock.jaspi.context;

import org.forgerock.auth.common.DebugLogger;
import org.forgerock.jaspi.logging.LogFactory;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import java.util.Map;

/**
 * Responsible for constructing and initialising ServerAuthModules.
 *
 * @since 1.3.0
 */
public enum ServerAuthModuleInstanceCreatorImpl implements ServerAuthModuleInstanceCreator {

    /**
     * The Singleton instance of the ServerAuthModuleInstanceCreatorImpl.
     */
    INSTANCE;

    private static final DebugLogger LOGGER = LogFactory.getDebug();

    /**
     * Constructs a new ServerAuthModule instance, using the given class name.
     * <p>
     * After constructing the ServerAuthModule, its ServerAuthModule#initialize method is called with the given
     * properties map, message policy and callback handler.
     *
     * @param className The class name of the ServerAuthModule.
     * @param moduleProperties A Map of properties used to initialise the ServerAuthModule.
     * @param messagePolicy The message policy used to initialise the ServerAuthModule.
     * @param handler The callback handler used to initialise the ServerAuthModule.
     * @return An instance of the ServerAuthModule.
     * @throws AuthException If the AuthConfigProvider class name is null or empty.
     */
    @Override
    public ServerAuthModule construct(final String className, final Map<String, Object> moduleProperties,
            final MessagePolicy messagePolicy, final CallbackHandler handler) throws AuthException {

        if (className != null && !className.isEmpty()) {
            try {
                ServerAuthModule module = (ServerAuthModule) Class.forName(className).newInstance();
                module.initialize(messagePolicy, null, handler, moduleProperties);
                LOGGER.debug("Created module, className: " + className);
                return module;
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to instantiate module, className: " + className);
                throw new AuthException("Failed to instantiate module, className: " + className);
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to instantiate module, className: " + className);
                throw new AuthException("Failed to instantiate module, className: " + className);
            } catch (InstantiationException e) {
                LOGGER.error("Failed to instantiate module, className: " + className);
                throw new AuthException("Failed to instantiate module, className: " + className);
            }
        }

        LOGGER.error("FClass name cannot be null.");
        throw new AuthException("Class name cannot be null.");
    }
}
