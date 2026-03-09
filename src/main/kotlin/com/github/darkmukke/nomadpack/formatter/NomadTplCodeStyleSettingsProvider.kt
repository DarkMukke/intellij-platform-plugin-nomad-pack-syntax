package com.github.darkmukke.nomadpack.formatter

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

/**
 * Provides the code style configuration page for Nomad Template files
 */
class NomadTplCodeStyleSettingsProvider : CodeStyleSettingsProvider() {

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings? {
        return null  // We don't need custom settings, just use common settings
    }

    override fun getConfigurableDisplayName(): String {
        return "Nomad Template"
    }

    override fun getConfigurableId(): String {
        return "preferences.sourceCode.NomadTemplate"
    }

    override fun createConfigurable(
        settings: CodeStyleSettings,
        modelSettings: CodeStyleSettings
    ): CodeStyleConfigurable {
        return object : CodeStyleAbstractConfigurable(settings, modelSettings, configurableDisplayName) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return NomadTplCodeStyleMainPanel(currentSettings, settings)
            }
        }
    }

    private class NomadTplCodeStyleMainPanel(
        currentSettings: CodeStyleSettings,
        settings: CodeStyleSettings
    ) : TabbedLanguageCodeStylePanel(NomadTplLanguage, currentSettings, settings) {
        // This creates the standard tabs: Tabs and Indents, Spaces, Wrapping and Braces, etc.
    }
}
