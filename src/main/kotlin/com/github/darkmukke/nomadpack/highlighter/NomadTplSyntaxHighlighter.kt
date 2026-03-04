package com.github.darkmukke.nomadpack.highlighter

import com.github.darkmukke.nomadpack.lexer.NomadTplLexer
import com.github.darkmukke.nomadpack.lexer.NomadTplTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class NomadTplSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = NomadTplLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            NomadTplTokenTypes.TEMPLATE_START, 
            NomadTplTokenTypes.TEMPLATE_END,
            NomadTplTokenTypes.TEMPLATE_LEFT_TRIM,
            NomadTplTokenTypes.TEMPLATE_RIGHT_TRIM -> arrayOf(BRACES)
            
            NomadTplTokenTypes.KEYWORD -> arrayOf(KEYWORD)
            
            NomadTplTokenTypes.DOT,
            NomadTplTokenTypes.IDENTIFIER -> arrayOf(VARIABLE)
            
            NomadTplTokenTypes.FUNCTION -> arrayOf(FUNCTION_CALL)
            
            NomadTplTokenTypes.STRING_LITERAL -> arrayOf(STRING)
            
            NomadTplTokenTypes.NUMBER -> arrayOf(NUMBER)
            
            NomadTplTokenTypes.PIPE,
            NomadTplTokenTypes.LPAREN,
            NomadTplTokenTypes.RPAREN,
            NomadTplTokenTypes.COMMA,
            NomadTplTokenTypes.COLON,
            NomadTplTokenTypes.EQUALS -> arrayOf(OPERATOR)
            
            else -> emptyArray()
        }
    }

    companion object {
        val BRACES = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_BRACES",
            DefaultLanguageHighlighterColors.BRACES
        )
        
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        
        val VARIABLE = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_VARIABLE",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        
        val FUNCTION_CALL = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_FUNCTION",
            DefaultLanguageHighlighterColors.FUNCTION_CALL
        )
        
        val STRING = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_STRING",
            DefaultLanguageHighlighterColors.STRING
        )
        
        val NUMBER = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_NUMBER",
            DefaultLanguageHighlighterColors.NUMBER
        )
        
        val OPERATOR = TextAttributesKey.createTextAttributesKey(
            "NOMAD_TPL_OPERATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
    }
}
