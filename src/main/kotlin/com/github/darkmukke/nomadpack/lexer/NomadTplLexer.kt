package com.github.darkmukke.nomadpack.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class NomadTplLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var bufferEnd: Int = 0
    private var state: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null

    companion object {
        private const val STATE_NORMAL = 0
        private const val STATE_IN_TEMPLATE = 1
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.bufferEnd = endOffset
        this.state = initialState
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        advance()
    }

    override fun getState(): Int = state

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        if (tokenEnd >= bufferEnd) {
            tokenType = null
            return
        }

        tokenStart = tokenEnd

        when (state) {
            STATE_NORMAL -> {
                val templateStart = findTemplateStart(tokenStart)
                if (templateStart == tokenStart) {
                    // We're at the start of a template expression
                    lexTemplateDelimiter()
                } else if (templateStart > tokenStart) {
                    // Tokenize content until template start
                    lexNormalContent(templateStart)
                } else {
                    // No more templates, tokenize rest of content
                    lexNormalContent(bufferEnd)
                }
            }
            STATE_IN_TEMPLATE -> {
                lexInsideTemplate()
            }
        }
    }

    private fun lexNormalContent(endPos: Int) {
        // Skip whitespace
        while (tokenEnd < endPos && Character.isWhitespace(buffer[tokenEnd])) {
            tokenEnd++
        }

        if (tokenEnd > tokenStart) {
            tokenType = NomadTplTokenTypes.WHITE_SPACE
            return
        }

        tokenStart = tokenEnd

        // Check for specific characters
        when {
            tokenEnd < endPos && buffer[tokenEnd] == '{' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.LPAREN
                return
            }
            tokenEnd < endPos && buffer[tokenEnd] == '}' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.RPAREN
                return
            }
            tokenEnd < endPos && buffer[tokenEnd] == '=' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.EQUALS
                return
            }
            tokenEnd < endPos && buffer[tokenEnd] == '"' -> {
                lexStringLiteral()
                return
            }
            tokenEnd < endPos && buffer[tokenEnd] == '#' -> {
                // Comment - consume until end of line
                while (tokenEnd < endPos && buffer[tokenEnd] != '\n') {
                    tokenEnd++
                }
                tokenType = NomadTplTokenTypes.RAW_TEXT
                return
            }
        }

        // Check for numbers
        if (tokenEnd < endPos && Character.isDigit(buffer[tokenEnd])) {
            while (tokenEnd < endPos && (Character.isDigit(buffer[tokenEnd]) || buffer[tokenEnd] == '.')) {
                tokenEnd++
            }
            tokenType = NomadTplTokenTypes.NUMBER
            return
        }

        // Lex identifier or keyword
        if (tokenEnd < endPos && (Character.isJavaIdentifierStart(buffer[tokenEnd]) || buffer[tokenEnd] == '_')) {
            while (tokenEnd < endPos && (Character.isJavaIdentifierPart(buffer[tokenEnd]) || buffer[tokenEnd] == '_' || buffer[tokenEnd] == '-')) {
                tokenEnd++
            }

            val text = buffer.subSequence(tokenStart, tokenEnd).toString()
            tokenType = if (NomadTplTokenTypes.KEYWORDS.contains(text)) {
                NomadTplTokenTypes.KEYWORD
            } else {
                NomadTplTokenTypes.IDENTIFIER
            }
            return
        }

        // Unknown character, skip it
        if (tokenEnd < endPos) {
            tokenEnd++
            tokenType = NomadTplTokenTypes.RAW_TEXT
        }
    }

    private fun findTemplateStart(from: Int): Int {
        var pos = from
        while (pos < bufferEnd - 1) {
            if (buffer[pos] == '[' && buffer[pos + 1] == '[') {
                return pos
            }
            pos++
        }
        return -1
    }

    private fun findTemplateEnd(from: Int): Int {
        var pos = from
        while (pos < bufferEnd - 1) {
            if (buffer[pos] == ']' && buffer[pos + 1] == ']') {
                return pos
            }
            pos++
        }
        return -1
    }

    private fun lexTemplateDelimiter() {
        if (tokenStart + 1 < bufferEnd && buffer[tokenStart] == '[' && buffer[tokenStart + 1] == '[') {
            tokenEnd = tokenStart + 2
            tokenType = NomadTplTokenTypes.TEMPLATE_START
            
            // Check for left trim marker
            if (tokenEnd < bufferEnd && buffer[tokenEnd] == '-') {
                tokenEnd++
                tokenType = NomadTplTokenTypes.TEMPLATE_LEFT_TRIM
            }
            
            state = STATE_IN_TEMPLATE
        }
    }

    private fun lexInsideTemplate() {
        // Skip whitespace
        while (tokenEnd < bufferEnd && Character.isWhitespace(buffer[tokenEnd])) {
            tokenEnd++
        }

        if (tokenEnd > tokenStart) {
            tokenType = NomadTplTokenTypes.WHITE_SPACE
            return
        }

        tokenStart = tokenEnd

        // Check for template end
        if (tokenEnd < bufferEnd - 1 && buffer[tokenEnd] == ']' && buffer[tokenEnd + 1] == ']') {
            tokenEnd += 2
            tokenType = NomadTplTokenTypes.TEMPLATE_END
            state = STATE_NORMAL
            return
        }

        // Check for right trim before ]]
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '-') {
            val nextPos = tokenEnd + 1
            if (nextPos < bufferEnd - 1 && buffer[nextPos] == ']' && buffer[nextPos + 1] == ']') {
                tokenEnd++
                tokenType = NomadTplTokenTypes.TEMPLATE_RIGHT_TRIM
                return
            }
        }

        // Check for specific tokens
        when (buffer[tokenEnd]) {
            '.' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.DOT
                return
            }
            '|' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.PIPE
                return
            }
            '(' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.LPAREN
                return
            }
            ')' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.RPAREN
                return
            }
            ',' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.COMMA
                return
            }
            ':' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.COLON
                return
            }
            '=' -> {
                tokenEnd++
                tokenType = NomadTplTokenTypes.EQUALS
                return
            }
            '"' -> {
                lexStringLiteral()
                return
            }
        }

        // Check for numbers
        if (Character.isDigit(buffer[tokenEnd])) {
            while (tokenEnd < bufferEnd && (Character.isDigit(buffer[tokenEnd]) || buffer[tokenEnd] == '.')) {
                tokenEnd++
            }
            tokenType = NomadTplTokenTypes.NUMBER
            return
        }

        // Lex identifier or keyword
        if (Character.isJavaIdentifierStart(buffer[tokenEnd]) || buffer[tokenEnd] == '_') {
            while (tokenEnd < bufferEnd && (Character.isJavaIdentifierPart(buffer[tokenEnd]) || buffer[tokenEnd] == '_')) {
                tokenEnd++
            }
            
            val text = buffer.subSequence(tokenStart, tokenEnd).toString()
            tokenType = if (NomadTplTokenTypes.KEYWORDS.contains(text)) {
                NomadTplTokenTypes.KEYWORD
            } else {
                // Check if this looks like a function call (followed by '(' or '|')
                var lookAhead = tokenEnd
                while (lookAhead < bufferEnd && Character.isWhitespace(buffer[lookAhead])) {
                    lookAhead++
                }
                if (lookAhead < bufferEnd && (buffer[lookAhead] == '(' || buffer[lookAhead] == '|')) {
                    NomadTplTokenTypes.FUNCTION
                } else {
                    NomadTplTokenTypes.IDENTIFIER
                }
            }
            return
        }

        // Unknown character, skip it
        tokenEnd++
        tokenType = NomadTplTokenTypes.IDENTIFIER
    }

    private fun lexStringLiteral() {
        tokenEnd++ // Skip opening quote
        
        while (tokenEnd < bufferEnd) {
            when (buffer[tokenEnd]) {
                '"' -> {
                    tokenEnd++ // Include closing quote
                    tokenType = NomadTplTokenTypes.STRING_LITERAL
                    return
                }
                '\\' -> {
                    tokenEnd++ // Skip escape char
                    if (tokenEnd < bufferEnd) {
                        tokenEnd++ // Skip escaped char
                    }
                }
                else -> tokenEnd++
            }
        }
        
        // Unclosed string
        tokenType = NomadTplTokenTypes.STRING_LITERAL
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd
}
