/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock Inc. All rights reserved.
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

package org.forgerock.script.source;

import org.forgerock.script.ScriptName;

/**
 * A SourceContainer represent the collection of script source.
 * <p/>
 * The implementation can fetch the {@code ScriptSource} from a directory, a
 * bundle(jar) or any other container.
 * 
 * @author Laszlo Hordos
 */
public interface SourceContainer extends SourceUnit {

    ScriptSource findScriptSource(ScriptName name);

}
