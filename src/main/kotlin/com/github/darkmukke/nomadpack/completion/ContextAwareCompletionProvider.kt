package com.github.darkmukke.nomadpack.completion

import com.github.darkmukke.nomadpack.structure.NomadStanzaHierarchy
import com.github.darkmukke.nomadpack.structure.PropertyType
import com.github.darkmukke.nomadpack.structure.StanzaContext
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

/**
 * Provides context-aware completion based on block hierarchy
 */
class ContextAwareCompletionProvider : CompletionProvider<CompletionParameters>() {

    companion object {
        // Cache the regex pattern for performance
        private val stanzaBlockPattern by lazy {
            Regex(NomadStanzaHierarchy.buildStanzaMatchPattern())
        }
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        val blockContext = determineBlockContext(position)
        
        // Add valid stanzas for this context
        val validStanzas = NomadStanzaHierarchy.getValidStanzas(blockContext)
        for (stanza in validStanzas) {
            result.addElement(
                LookupElementBuilder.create(stanza.name)
                    .withIcon(AllIcons.Nodes.Class)
                    .withTypeText(getContextName(blockContext))
                    .withTailText(" { ... }", true)
                    .withInsertHandler { ctx, _ ->
                        val document = ctx.document
                        val offset = ctx.tailOffset
                        // Insert opening brace and newline
                        document.insertString(offset, " {\n  \n}")
                        ctx.editor.caretModel.moveToOffset(offset + 4)
                    }
                    .bold()
            )
        }
        
        // Add valid properties for this context
        val validProperties = NomadStanzaHierarchy.getValidProperties(blockContext)

        // If we have very few properties (suggests wrong context detection),
        // also include commonly used properties as fallback
        val propertiesToShow = if (validProperties.size < 3 && blockContext != StanzaContext.ROOT) {
            // Include properties from potential parent contexts as fallback
            validProperties + NomadStanzaHierarchy.properties.filter {
                it.name in setOf("name", "port", "tags", "type", "driver", "count")
            }
        } else {
            validProperties
        }.distinctBy { it.name }

        for (property in propertiesToShow) {
            val icon = when (property.type) {
                PropertyType.STRING -> AllIcons.Nodes.Property
                PropertyType.NUMBER -> AllIcons.Nodes.Field
                PropertyType.BOOLEAN -> AllIcons.Nodes.Variable
                PropertyType.ARRAY_STRING, PropertyType.ARRAY_NUMBER -> AllIcons.Nodes.DataTables
                PropertyType.MAP -> AllIcons.Nodes.DataTables
                PropertyType.BLOCK -> AllIcons.Nodes.Class
                PropertyType.DURATION -> AllIcons.Nodes.Field
                PropertyType.EXPRESSION -> AllIcons.Nodes.Lambda
            }
            
            val typeText = buildString {
                append(property.type.name.lowercase().replace("_", " "))
                if (property.required) append(" (required)")
                if (property.defaultValue != null) append(" = ${property.defaultValue}")
            }
            
            val insertText = when {
                property.validValues != null -> "${property.name} = \"${property.validValues.first()}\""
                property.type == PropertyType.STRING -> "${property.name} = \"\""
                property.type == PropertyType.NUMBER -> "${property.name} = ${property.defaultValue ?: "0"}"
                property.type == PropertyType.BOOLEAN -> "${property.name} = ${property.defaultValue ?: "false"}"
                property.type == PropertyType.ARRAY_STRING -> "${property.name} = []"
                property.type == PropertyType.ARRAY_NUMBER -> "${property.name} = []"
                property.type == PropertyType.MAP -> "${property.name} = {}"
                property.type == PropertyType.DURATION -> "${property.name} = \"${property.defaultValue ?: "10s"}\""
                property.type == PropertyType.EXPRESSION -> "${property.name} = [[ ]]"
                else -> "${property.name} = "
            }
            
            result.addElement(
                LookupElementBuilder.create(property.name)
                    .withIcon(icon)
                    .withTypeText(typeText, true)
                    .withTailText(if (property.description.isNotEmpty()) " - ${property.description}" else "", true)
                    .withInsertHandler { ctx, _ ->
                        val document = ctx.document
                        val start = ctx.startOffset
                        val end = ctx.tailOffset
                        document.replaceString(start, end, insertText)
                        
                        // Position cursor based on type
                        val cursorOffset = when {
                            property.type == PropertyType.STRING && property.validValues == null -> 
                                start + insertText.indexOf("\"\"") + 1
                            property.type == PropertyType.ARRAY_STRING || property.type == PropertyType.ARRAY_NUMBER ->
                                start + insertText.indexOf("[]") + 1
                            property.type == PropertyType.MAP ->
                                start + insertText.indexOf("{}") + 1
                            property.type == PropertyType.EXPRESSION ->
                                start + insertText.indexOf("[[ ]]") + 3
                            else -> start + insertText.length
                        }
                        ctx.editor.caretModel.moveToOffset(cursorOffset)
                    }
            )
            
            // If property has valid values, add them as separate completions
            property.validValues?.forEach { value ->
                result.addElement(
                    LookupElementBuilder.create("${ property.name} = \"$value\"")
                        .withPresentableText(value)
                        .withIcon(AllIcons.Nodes.Constant)
                        .withTypeText("${property.name} value", true)
                        .withLookupString(value)
                )
            }
        }
    }
    
    private fun determineBlockContext(element: PsiElement): StanzaContext {
        val blockNames = mutableListOf<String>()
        var current: PsiElement? = element.parent

        // Walk up the PSI tree to find block names
        var depth = 0
        while (current != null && depth < 30) {  // Increased depth for nested templates
            val text = current.text

            // Skip very large elements (performance)
            if (text.length > 50000) {
                current = current.parent
                depth++
                continue
            }

            // Remove template expressions [[ ]] to avoid false matches
            val cleanedText = removeTemplateExpressions(text)

            // Try to extract block name using the cached regex pattern
            val blockMatch = stanzaBlockPattern.find(cleanedText)

            if (blockMatch != null) {
                val stanzaName = blockMatch.groupValues[1]
                // Only add if not already in the list (avoid duplicates from nested PSI)
                if (blockNames.isEmpty() || blockNames[0] != stanzaName) {
                    blockNames.add(0, stanzaName)
                }
            }

            current = current.parent
            depth++
        }

        return NomadStanzaHierarchy.determineContext(blockNames)
    }

    /**
     * Remove template expressions [[ ]] from text to avoid interfering with block detection
     * This handles nested brackets and preserves the rest of the text structure
     */
    private fun removeTemplateExpressions(text: String): String {
        var result = text
        var startIndex = 0

        while (startIndex < result.length) {
            val openIndex = result.indexOf("[[", startIndex)
            if (openIndex == -1) break

            // Find matching closing bracket
            var depth = 1
            var closeIndex = openIndex + 2

            while (closeIndex < result.length - 1 && depth > 0) {
                if (result[closeIndex] == '[' && result[closeIndex + 1] == '[') {
                    depth++
                    closeIndex += 2
                } else if (result[closeIndex] == ']' && result[closeIndex + 1] == ']') {
                    depth--
                    closeIndex += 2
                } else {
                    closeIndex++
                }
            }

            if (depth == 0) {
                // Replace template expression with spaces to preserve positions
                val spaces = " ".repeat(closeIndex - openIndex)
                result = result.substring(0, openIndex) + spaces + result.substring(closeIndex)
                startIndex = openIndex + spaces.length
            } else {
                // Unmatched opening bracket, skip it
                startIndex = openIndex + 2
            }
        }

        return result
    }
    
    private fun getContextName(context: StanzaContext): String {
        return when (context) {
            StanzaContext.ROOT -> "Top-level"
            StanzaContext.JOB -> "Job block"
            StanzaContext.GROUP -> "Group block"
            StanzaContext.TASK -> "Task block"
            StanzaContext.SERVICE -> "Service block"
            StanzaContext.CHECK -> "Check block"
            StanzaContext.NETWORK -> "Network block"
            StanzaContext.PORT -> "Port block"
            StanzaContext.RESOURCES -> "Resources block"
            StanzaContext.VAULT -> "Vault block"
            StanzaContext.TEMPLATE -> "Template block"
            StanzaContext.CONSTRAINT -> "Constraint block"
            StanzaContext.AFFINITY -> "Affinity block"
            StanzaContext.SPREAD -> "Spread block"
            StanzaContext.UPDATE -> "Update block"
            StanzaContext.MIGRATE -> "Migrate block"
            StanzaContext.RESCHEDULE -> "Reschedule block"
            StanzaContext.RESTART -> "Restart block"
            StanzaContext.EPHEMERAL_DISK -> "Ephemeral disk block"
            StanzaContext.PERIODIC -> "Periodic block"
            StanzaContext.PARAMETERIZED -> "Parameterized block"
            StanzaContext.MULTIREGION -> "Multiregion block"
            StanzaContext.VOLUME -> "Volume block"
            StanzaContext.VOLUME_MOUNT -> "Volume mount block"
            StanzaContext.LOGS -> "Logs block"
            StanzaContext.ARTIFACT -> "Artifact block"
            StanzaContext.LIFECYCLE -> "Lifecycle block"
            StanzaContext.CONFIG -> "Config block"
            StanzaContext.ENV -> "Env block"
            StanzaContext.CONNECT -> "Connect block"
            StanzaContext.SIDECAR_SERVICE -> "Sidecar service block"
            StanzaContext.SIDECAR_TASK -> "Sidecar task block"
            StanzaContext.PROXY -> "Proxy block"
            StanzaContext.UPSTREAM -> "Upstream block"
            StanzaContext.GATEWAY -> "Gateway block"
            StanzaContext.INGRESS -> "Ingress block"
            StanzaContext.TERMINATING -> "Terminating block"
            StanzaContext.DNS -> "DNS block"
            StanzaContext.CSI_PLUGIN -> "CSI plugin block"
            StanzaContext.SCALING -> "Scaling block"
            StanzaContext.IDENTITY -> "Identity block"
            StanzaContext.ACTION -> "Action block"
            StanzaContext.SCHEDULE -> "Schedule block"
            StanzaContext.DEVICE -> "Device block"
            StanzaContext.NUMA -> "NUMA block"
            StanzaContext.HEADER -> "Header block"
            StanzaContext.CHECK_RESTART -> "Check restart block"
            StanzaContext.EXPOSE -> "Expose block"
            StanzaContext.TRANSPARENT_PROXY -> "Transparent proxy block"
            StanzaContext.MESH_GATEWAY -> "Mesh gateway block"
            StanzaContext.TARGET -> "Target block"
            StanzaContext.WAIT -> "Wait block"
            StanzaContext.STRATEGY -> "Strategy block"
            StanzaContext.REGION -> "Region block"
            StanzaContext.LISTENER -> "Listener block"
            StanzaContext.TLS -> "TLS block"
            StanzaContext.MESH -> "Mesh block"
            StanzaContext.PATH -> "Path block"
            StanzaContext.MOUNT_OPTIONS -> "Mount options block"
            StanzaContext.META -> "Meta block"
            StanzaContext.CONSUL -> "Consul block"
            StanzaContext.DISCONNECT -> "Disconnect block"
            StanzaContext.CNI -> "CNI block"
            StanzaContext.OPTIONS -> "Options block"
            StanzaContext.HEADERS -> "Headers block"
            StanzaContext.POLICY -> "Policy block"
            StanzaContext.DISPATCH_PAYLOAD -> "Dispatch payload block"
            StanzaContext.CHANGE_SCRIPT -> "Change script block"
        }
    }
}
