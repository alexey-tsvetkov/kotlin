/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.facet

import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import org.jetbrains.kotlin.config.KotlinCompilerInfo
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCompilerConfigurableTab

class KotlinFacetEditorCompilerTab(
        compilerInfo: KotlinCompilerInfo,
        editorContext: FacetEditorContext
) : FacetEditorTab() {
    val compilerConfigurable = KotlinCompilerConfigurableTab(
            editorContext.project,
            compilerInfo.commonCompilerArguments,
            compilerInfo.k2jsCompilerArguments,
            compilerInfo.compilerSettings,
            null,
            null,
            false
    )

    override fun apply() = compilerConfigurable.apply()

    override fun getDisplayName() = "Compiler"

    override fun createComponent() = compilerConfigurable.createComponent()!!

    override fun disposeUIResources() = compilerConfigurable.disposeUIResources()

    override fun isModified() = compilerConfigurable.isModified

    override fun reset() = compilerConfigurable.reset()
}