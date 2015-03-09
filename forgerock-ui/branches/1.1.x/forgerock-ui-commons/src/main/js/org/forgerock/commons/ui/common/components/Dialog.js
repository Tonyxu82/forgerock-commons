/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 ForgeRock AS. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

/*global define */

/**
 * @author mbilski
 */

define("org/forgerock/commons/ui/common/components/Dialog", [
    "jquery",
    "underscore",
    "org/forgerock/commons/ui/common/main/AbstractView",
    "org/forgerock/commons/ui/common/util/UIUtils",
    "org/forgerock/commons/ui/common/util/Constants",
    "org/forgerock/commons/ui/common/main/EventManager",
    "org/forgerock/commons/ui/common/main/Configuration"
], function($, _, AbstractView, uiUtils, constants, eventManager, conf) {
    /**
     * @exports org/forgerock/commons/ui/common/components/Dialog
     */
    var Dialog = AbstractView.extend({
        template: "templates/common/DialogTemplate.html",
        element: "#dialogs",
        mode: "append",

        events: {
            "click .dialogCloseCross":   "close",
            "click input[name='close']": "close"
        },

        actions: [
            {
                "type": "button",
                "name": "close"
            }
        ],

        /**
         * Creates new dialog in #dialogs div. Fills it with dialog template.
         * Then creates actions buttons and bind events. If actions map is empty, default
         * close action is added.
         */
        show: function(callback) {

            this.data.actions = this.actions;

            this.setElement($("#dialogs"));
            this.parentRender(_.bind(function() {

                this.$el.addClass('show');
                this.setElement(this.$el.find(".dialogContainer:last"));

                $("#dialog-background").addClass('show');
                this.$el.off('click').on('click', _.bind(this.bgClickToClose, this));

                this.loadContent(callback);
                this.delegateEvents();
            }, this));
        },

        /**
         * Loads template from 'contentTemplate'
         */
        loadContent: function(callback) {
            if(callback) {
                uiUtils.renderTemplate(this.data.theme.path + this.contentTemplate, this.$el.find(".dialogContent"), _.extend({}, conf.globalData, this.data), _.bind(callback, this), "append");
            } else {
                uiUtils.renderTemplate(this.data.theme.path + this.contentTemplate, this.$el.find(".dialogContent"), _.extend({}, conf.globalData, this.data), null, "append");
            }
        },

        render: function() {
            this.show();
        },

        bgClickToClose: function(e) {
            if (e.target === e.currentTarget || e.target.className === 'dialogCloseCross'){
                this.close(e);
            }
        },

        close: function(e) {
            if(e){ e.preventDefault(); }
            if ($(".dialogContainer").length < 2) {
                $("#dialog-background").removeClass('show');
                $("#dialogs").removeClass('show');
            }

            eventManager.sendEvent(constants.EVENT_DIALOG_CLOSE);
            this.$el.remove();
        },

        addAction: function(name, type) {
            if(!this.getAction(name)) {
                this.actions.push({
                    "name" : name,
                    "type" : type
                });
            }
        },

        getAction: function(name) {
            return _.find(this.actions, function(a) {
                return a.name === name;
            });
        }
    });

    return Dialog;
});
