package com.github.darkmukke.nomadpack.formatter

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider

/**
 * Provides code style settings for Nomad template files
 */
class NomadTplLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage(): Language = NomadTplLanguage

    override fun getLanguageName(): String = "Nomad Template"

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
            }
            SettingsType.INDENT_SETTINGS -> {
                consumer.showStandardOptions("INDENT_SIZE", "CONTINUATION_INDENT_SIZE", "TAB_SIZE", "USE_TAB_CHARACTER")
            }
            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions("KEEP_LINE_BREAKS", "KEEP_BLANK_LINES_IN_CODE")
            }
            else -> {}
        }
    }

    override fun getCodeSample(settingsType: SettingsType): String {
        return """job "example" {
  datacenters = ["dc1"]
  type = "service"

  group "app" {
    count = 1

    network {
      port "http" {
        to = 8000
      }
    }

    service {
      name = "example"
      port = "http"

      check {
        name = "alive"
        type = "http"
        path = "/"
        interval = "10s"
        timeout = "2s"
      }
    }

    task "server" {
      driver = "docker"

      config {
        image = "example:latest"
        ports = ["http"]
      }
    }
  }
}"""
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions
    ) {
        indentOptions.INDENT_SIZE = 2
        indentOptions.CONTINUATION_INDENT_SIZE = 2
        indentOptions.TAB_SIZE = 2
        indentOptions.USE_TAB_CHARACTER = false
    }
}
