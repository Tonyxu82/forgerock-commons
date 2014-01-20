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
 * Copyright 2012-2013 ForgeRock AS.
 */
package org.forgerock.json.resource.servlet;

import static org.forgerock.json.resource.servlet.HttpUtils.fail;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.forgerock.json.resource.servlet.Servlet2Adapter.Servlet2Synchronizer;

/**
 * An adapter for use in Servlet 3.x containers.
 */
final class Servlet3Adapter extends ServletApiVersionAdapter {
    /**
     * Synchronization implementation - only used when the container supports
     * asynchronous processing.
     */
    private final static class Servlet3Synchronizer implements ServletSynchronizer {
        private final AsyncContext asyncContext;
        private final HttpServletRequest httpRequest;
        private final HttpServletResponse httpResponse;

        private Servlet3Synchronizer(final HttpServletRequest httpRequest,
                final HttpServletResponse httpResponse) {
            this.httpRequest = httpRequest;
            this.httpResponse = httpResponse;
            if (httpRequest.isAsyncStarted()) {
                this.asyncContext = httpRequest.getAsyncContext();
            } else {
                this.asyncContext = httpRequest.startAsync();
                // Disable timeouts for certain containers - see http://java.net/jira/browse/GRIZZLY-1325
                asyncContext.setTimeout(0);
            }
        }

        @Override
        public void addAsyncListener(final Runnable runnable) {
            asyncContext.addListener(new AsyncListener() {

                @Override
                public void onComplete(final AsyncEvent event) throws IOException {
                    runnable.run();
                }

                @Override
                public void onError(final AsyncEvent event) throws IOException {
                    runnable.run();
                }

                @Override
                public void onStartAsync(final AsyncEvent event) throws IOException {
                    // Reregister.
                    event.getAsyncContext().addListener(this);
                }

                @Override
                public void onTimeout(final AsyncEvent event) throws IOException {
                    runnable.run();
                }
            });
        }

        @Override
        public void awaitIfNeeded() throws Exception {
            // Nothing to signal: this dispatcher is non-blocking.
        }

        @Override
        public boolean isAsync() {
            return true;
        }

        @Override
        public void signal() {
            // Nothing to signal: this dispatcher is non-blocking.
        }

        @Override
        public void signalAndComplete() {
            asyncContext.complete();
        }

        @Override
        public void signalAndComplete(final Throwable t) {
            fail(httpRequest, httpResponse, t);
            asyncContext.complete();
        }
    }

    Servlet3Adapter() {
        // Nothing to do.
    }

    @Override
    public ServletSynchronizer createServletSynchronizer(final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse) {
        if (httpRequest.isAsyncSupported()) {
            return new Servlet3Synchronizer(httpRequest, httpResponse);
        } else {
            // Fall-back to Servlet 2 blocking implementation.
            return new Servlet2Synchronizer(httpRequest, httpResponse);
        }
    }

}
