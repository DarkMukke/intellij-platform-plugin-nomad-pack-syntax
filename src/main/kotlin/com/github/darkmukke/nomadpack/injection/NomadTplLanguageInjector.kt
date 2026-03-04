package com.github.darkmukke.nomadpack.injection

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Injects NomadTpl language into [[ ]] template expressions in .nomad.tpl and .tpl files
 * This allows syntax highlighting of template expressions within HCL code
 */
class NomadTplLanguageInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val virtualFile = context.containingFile?.virtualFile ?: return
        val fileName = virtualFile.name

        // Only inject in Nomad Pack template files
        if (!fileName.endsWith(".nomad.tpl") && !fileName.endsWith(".tpl")) return

        // Work with leaf elements (tokens) from HCL
        if (context !is LeafPsiElement) return

        val text = context.text
        if (!text.contains("[[") || !text.contains("]]")) return

        // Find all [[ ]] regions and inject a template language
        var startIndex = 0
        while (true) {
            val openIndex = text.indexOf("[[", startIndex)
            if (openIndex == -1) break

            val closeIndex = text.indexOf("]]", openIndex + 2)
            if (closeIndex == -1) break

            // Inject template language into this range
            val rangeStart = openIndex
            val rangeEnd = closeIndex + 2

            try {
                registrar.startInjecting(NomadTplLanguage)
                registrar.addPlace(
                    null,
                    null,
                    context as com.intellij.psi.PsiLanguageInjectionHost,
                    TextRange(rangeStart, rangeEnd)
                )
                registrar.doneInjecting()
            } catch (e: Exception) {
                // If the injection fails, skip this element
            }

            startIndex = closeIndex + 2
        }
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        // Inject into all PSI elements to catch HCL tokens containing [[ ]]
        return listOf(PsiElement::class.java)
    }
}
