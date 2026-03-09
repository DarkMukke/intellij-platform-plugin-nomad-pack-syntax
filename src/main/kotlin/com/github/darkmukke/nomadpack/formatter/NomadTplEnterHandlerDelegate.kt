package com.github.darkmukke.nomadpack.formatter

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile
import com.github.darkmukke.nomadpack.psi.NomadTplFile

/**
 * Handles Enter key press to provide automatic indentation in Nomad template files
 */
class NomadTplEnterHandlerDelegate : EnterHandlerDelegateAdapter() {

    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffsetRef: Ref<Int>,
        caretAdvance: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): EnterHandlerDelegate.Result {
        if (file !is NomadTplFile) {
            return EnterHandlerDelegate.Result.Continue
        }

        val caretOffset = caretOffsetRef.get()
        val document = editor.document
        val text = document.text

        // Check if we just pressed Enter after an opening brace
        // Look backwards from caret to find if we're after a '{'
        var i = caretOffset - 1
        var foundOpenBrace = false

        while (i >= 0 && i >= caretOffset - 50) {
            val char = text[i]
            when {
                char == '{' -> {
                    foundOpenBrace = true
                    break
                }
                char == '}' -> break  // Found closing brace first, not inside a block
                char.isWhitespace() -> i--  // Skip whitespace
                else -> break  // Found other character
            }
        }

        // If we found an opening brace, let the formatter handle indentation
        return if (foundOpenBrace) {
            EnterHandlerDelegate.Result.Continue
        } else {
            EnterHandlerDelegate.Result.Continue
        }
    }
}
