package com.github.darkmukke.nomadpack.formatter

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.intellij.psi.codeStyle.lineIndent.LineIndentProvider

/**
 * Provides automatic indentation when user presses Enter
 */
class NomadTplLineIndentProvider : LineIndentProvider {

    override fun getLineIndent(project: Project, editor: Editor, language: Language?, offset: Int): String? {
        if (language != NomadTplLanguage) {
            return null
        }

        // Get code style settings to read indent size
        val settings = CodeStyleSettingsManager.getSettings(project)
        val indentOptions = settings.getIndentOptions(NomadTplLanguage.associatedFileType)
        val indentSize = indentOptions.INDENT_SIZE
        val useTab = indentOptions.USE_TAB_CHARACTER

        val document = editor.document
        val text = document.text

        // Find the current line start
        val lineNumber = document.getLineNumber(offset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)

        // Look backwards from the cursor to find the previous non-empty line
        var prevLineNum = lineNumber - 1
        var prevLineContent = ""
        var prevLineIndent = ""

        while (prevLineNum >= 0) {
            val prevLineStart = document.getLineStartOffset(prevLineNum)
            val prevLineEnd = document.getLineEndOffset(prevLineNum)
            val line = text.substring(prevLineStart, prevLineEnd)

            if (line.isNotBlank()) {
                prevLineContent = line
                // Calculate indent of previous line
                prevLineIndent = line.takeWhile { it.isWhitespace() }
                break
            }
            prevLineNum--
        }

        if (prevLineContent.isEmpty()) {
            return null  // No previous line found, use default behavior
        }

        // Check if previous line ends with opening brace or define/range
        val trimmedPrevLine = prevLineContent.trim()

        // Create indent string based on settings
        val indentString = if (useTab) "\t" else " ".repeat(indentSize)

        when {
            // Previous line ends with {
            trimmedPrevLine.endsWith("{") -> {
                return prevLineIndent + indentString
            }

            // Previous line is a template define or range start
            trimmedPrevLine.matches(Regex(""".*\[\[\s*(define|range).*""")) && !trimmedPrevLine.contains("end") -> {
                return prevLineIndent + indentString
            }

            // Current line starts with } or [[ end ]] - dedent
            else -> {
                val currentLineContent = text.substring(lineStartOffset, offset).trim()
                if (currentLineContent.startsWith("}") ||
                    (currentLineContent.startsWith("[[") && currentLineContent.contains("end"))) {
                    // Remove one indent level from previous line's indent
                    return if (prevLineIndent.length >= indentSize) {
                        prevLineIndent.substring(indentSize)
                    } else {
                        ""
                    }
                }
            }
        }

        // Default: maintain previous line's indent
        return null  // Let IntelliJ handle it
    }

    override fun isSuitableFor(language: Language?): Boolean {
        return language == NomadTplLanguage
    }
}
