package com.github.darkmukke.nomadpack.markdown

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.Language
import org.intellij.plugins.markdown.injection.CodeFenceLanguageProvider

/**
 * Provides language injection for ```nomadpack code fences in Markdown
 *
 * Supports these code fence identifiers:
 * - ```nomadpack
 * - ```nomad-pack
 * - ```nomad.tpl
 * - ```nomad
 */
class NomadPackCodeFenceLanguageProvider : CodeFenceLanguageProvider {
    override fun getLanguageByInfoString(infoString: String): Language? {
        return when (infoString.lowercase()) {
            "nomadpack", "nomad-pack", "nomad.tpl", "nomad" -> NomadTplLanguage
            else -> null
        }
    }

    override fun getCompletionVariantsForInfoString(parameters: CompletionParameters): MutableList<LookupElement> {
        return mutableListOf(
            LookupElementBuilder.create("nomadpack")
                .withTypeText("Nomad Pack")
                .withTailText(" (Nomad Pack template syntax)", true),
            LookupElementBuilder.create("nomad-pack")
                .withTypeText("Nomad Pack")
                .withTailText(" (alternative)", true),
            LookupElementBuilder.create("nomad.tpl")
                .withTypeText("Nomad Pack")
                .withTailText(" (template file)", true),
            LookupElementBuilder.create("nomad")
                .withTypeText("Nomad Pack")
                .withTailText(" (job specification)", true)
        )
    }
}
