package com.github.darkmukke.nomadpack.formatter

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.github.darkmukke.nomadpack.lexer.NomadTplTokenTypes
import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings

/**
 * Formatting model builder for Nomad template files
 * Handles indentation for HCL blocks and Go template define blocks
 */
class NomadTplFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings

        val spacingBuilder = createSpacingBuilder(settings)
        // Root block: parent = null, indent = none (like Terraform)
        val block: Block = NomadTplBlock(
            parent = null,
            node = formattingContext.node,
            settings = settings,
            spacingBuilder = spacingBuilder,
            indent = Indent.getNoneIndent(),
            alignment = null,
            wrap = null
        )

        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            block,
            settings
        )
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
        val commonSettings = settings.getCommonSettings(NomadTplLanguage)

        return SpacingBuilder(settings, NomadTplLanguage)
            // Spacing around equals
            .before(NomadTplTokenTypes.EQUALS).spaces(1)
            .after(NomadTplTokenTypes.EQUALS).spaces(1)
            // Spacing around commas
            .before(NomadTplTokenTypes.COMMA).none()
            .after(NomadTplTokenTypes.COMMA).spaces(1)
            // Template expression spacing
            .after(NomadTplTokenTypes.TEMPLATE_START).spaces(1)
            .before(NomadTplTokenTypes.TEMPLATE_END).spaces(1)
    }

    override fun getRangeAffectingIndent(file: PsiFile, offset: Int, elementAtOffset: ASTNode): TextRange? {
        return null
    }
}

/**
 * Formatting block for Nomad template syntax
 * Implements Block interface with proper whitespace filtering for live preview
 * Follows Terraform HCL plugin pattern with parent tracking
 */
internal class NomadTplBlock(
    private val parent: NomadTplBlock?,
    private val node: ASTNode,
    private val settings: CodeStyleSettings,
    private val spacingBuilder: SpacingBuilder,
    private val indent: Indent?,
    private val alignment: Alignment? = null,
    private val wrap: Wrap? = null
) : Block {

    override fun getTextRange(): TextRange = node.textRange

    override fun getSubBlocks(): List<Block> {
        // Performance safeguard for very large files
        if (node.textLength > 50000) {
            return emptyList()
        }

        // Filter whitespace nodes and create blocks for significant children
        // This is critical for live preview to work correctly
        return node.getChildren(null)
            .filter { child ->
                child.elementType != TokenType.WHITE_SPACE &&
                child.textLength > 0 &&
                child.text.isNotBlank()
            }
            .map { child -> makeSubBlock(child) }
    }

    private fun makeSubBlock(childNode: ASTNode): NomadTplBlock {
        val childIndent = calculateIndentForChild(childNode)

        return NomadTplBlock(
            parent = this,
            node = childNode,
            settings = settings,
            spacingBuilder = spacingBuilder,
            indent = childIndent,
            alignment = null,
            wrap = null
        )
    }

    private fun calculateIndentForChild(childNode: ASTNode): Indent {
        val childText = childNode.text.trim()

        // Children of file root get no indent (like Terraform)
        if (node.isFile()) {
            return Indent.getNoneIndent()
        }

        // Braces themselves don't get indented
        if (childText == "{" || childText == "}") {
            return Indent.getNoneIndent()
        }

        // Closing template blocks
        if (childText.startsWith("[[") && childText.contains("end")) {
            return Indent.getNoneIndent()
        }

        // Content inside blocks gets normal indent
        // Detect if this node is a container (has braces)
        if (node.text.contains("{")) {
            return Indent.getNormalIndent()
        }

        return Indent.getNoneIndent()
    }

    private fun ASTNode.isFile(): Boolean {
        return this.elementType is com.intellij.psi.tree.IFileElementType
    }

    override fun getWrap(): Wrap? = wrap

    override fun getIndent(): Indent? = indent

    override fun getAlignment(): Alignment? = alignment

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        // This determines what indent new children should get
        // Used for Enter key and live preview formatting

        if (node.textLength > 10000) {
            return ChildAttributes(Indent.getNoneIndent(), null)
        }

        // File-level children get no indent
        if (node.isFile()) {
            return ChildAttributes(Indent.getNoneIndent(), null)
        }

        // If this node contains braces, its children should be indented
        if (node.text.contains("{")) {
            return ChildAttributes(Indent.getNormalIndent(), null)
        }

        // Template define/range blocks indent their children
        val text = node.text
        if (text.contains("[[") && (text.contains("define") || text.contains("range")) && !text.contains("end")) {
            return ChildAttributes(Indent.getNormalIndent(), null)
        }

        return ChildAttributes(Indent.getNoneIndent(), null)
    }

    override fun isIncomplete(): Boolean = false

    override fun isLeaf(): Boolean = node.firstChildNode == null
}
