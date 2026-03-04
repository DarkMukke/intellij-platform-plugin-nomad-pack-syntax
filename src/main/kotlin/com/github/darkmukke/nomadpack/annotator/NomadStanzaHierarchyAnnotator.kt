package com.github.darkmukke.nomadpack.annotator

import com.github.darkmukke.nomadpack.structure.NomadStanzaHierarchy
import com.github.darkmukke.nomadpack.structure.StanzaContext
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

/**
 * Annotates invalid stanza placements in Nomad job specifications
 * Highlights errors when stanzas are placed in invalid parent contexts
 */
class NomadStanzaHierarchyAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        // Performance optimization: Only process elements that could be block declarations
        // Skip whitespace, comments, and other non-relevant elements
        val text = element.text
        if (text.isBlank() || text.length < 3) return

        // Quick check: must contain both identifier-like text and opening brace
        if (!text.contains('{')) return

        // Match block patterns like: "stanza_name" { or stanza_name "label" {
        // Only match at the start of the element to avoid false positives
        val blockPattern = Regex("""^\s*([\w_]+)\s*(?:"[^"]*")?\s*\{""")
        val match = blockPattern.find(text) ?: return

        val stanzaName = match.groupValues[1]

        // Skip if this is not a recognized stanza (quick lookup in set)
        if (!isRecognizedStanza(stanzaName)) return

        // Determine parent context
        val parentContext = determineParentContext(element)

        // Check if this stanza is valid in the parent context
        val validStanzas = NomadStanzaHierarchy.getValidStanzas(parentContext)
        val isValid = validStanzas.any { it.name == stanzaName }

        if (!isValid) {
            val validStanzaNames = validStanzas.map { it.name }
            val parentName = getContextDisplayName(parentContext)

            val message = buildString {
                append("Stanza '$stanzaName' is not valid in $parentName")
                if (validStanzaNames.isNotEmpty()) {
                    append(". Valid stanzas: ${validStanzaNames.joinToString(", ")}")
                }
            }

            holder.newAnnotation(HighlightSeverity.ERROR, message)
                .range(element)
                .create()
        }
    }

    private fun isRecognizedStanza(name: String): Boolean {
        return NomadStanzaHierarchy.getAllStanzaNames().contains(name)
    }

    private fun determineParentContext(element: PsiElement): StanzaContext {
        val blockNames = mutableListOf<String>()
        var current: PsiElement? = element.parent

        // Walk up the PSI tree to find parent block names
        var depth = 0
        while (current != null && depth < 20) {
            val text = current.text

            // Skip if this is the same element (avoid matching itself)
            if (current == element) {
                current = current.parent
                depth++
                continue
            }

            // Remove template expressions [[ ]] to avoid false matches
            val cleanedText = removeTemplateExpressions(text)

            // Try to extract block name using the cached regex pattern
            val blockMatch = stanzaBlockPattern.find(cleanedText)

            if (blockMatch != null) {
                blockNames.add(0, blockMatch.groupValues[1])
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

    private fun getContextDisplayName(context: StanzaContext): String {
        return when (context) {
            StanzaContext.ROOT -> "file root (top-level)"
            StanzaContext.JOB -> "job block"
            StanzaContext.GROUP -> "group block"
            StanzaContext.TASK -> "task block"
            StanzaContext.SERVICE -> "service block"
            StanzaContext.CHECK -> "check block"
            StanzaContext.NETWORK -> "network block"
            StanzaContext.PORT -> "port block"
            StanzaContext.RESOURCES -> "resources block"
            StanzaContext.VAULT -> "vault block"
            StanzaContext.TEMPLATE -> "template block"
            StanzaContext.CONSTRAINT -> "constraint block"
            StanzaContext.AFFINITY -> "affinity block"
            StanzaContext.SPREAD -> "spread block"
            StanzaContext.UPDATE -> "update block"
            StanzaContext.MIGRATE -> "migrate block"
            StanzaContext.RESCHEDULE -> "reschedule block"
            StanzaContext.RESTART -> "restart block"
            StanzaContext.EPHEMERAL_DISK -> "ephemeral_disk block"
            StanzaContext.PERIODIC -> "periodic block"
            StanzaContext.PARAMETERIZED -> "parameterized block"
            StanzaContext.MULTIREGION -> "multiregion block"
            StanzaContext.VOLUME -> "volume block"
            StanzaContext.VOLUME_MOUNT -> "volume_mount block"
            StanzaContext.LOGS -> "logs block"
            StanzaContext.ARTIFACT -> "artifact block"
            StanzaContext.LIFECYCLE -> "lifecycle block"
            StanzaContext.CONFIG -> "config block"
            StanzaContext.ENV -> "env block"
            StanzaContext.CONNECT -> "connect block"
            StanzaContext.SIDECAR_SERVICE -> "sidecar_service block"
            StanzaContext.SIDECAR_TASK -> "sidecar_task block"
            StanzaContext.PROXY -> "proxy block"
            StanzaContext.UPSTREAM -> "upstreams block"
            StanzaContext.GATEWAY -> "gateway block"
            StanzaContext.INGRESS -> "ingress block"
            StanzaContext.TERMINATING -> "terminating block"
            StanzaContext.DNS -> "dns block"
            StanzaContext.CSI_PLUGIN -> "csi_plugin block"
            StanzaContext.SCALING -> "scaling block"
            StanzaContext.IDENTITY -> "identity block"
            StanzaContext.ACTION -> "action block"
            StanzaContext.SCHEDULE -> "schedule block"
            StanzaContext.DEVICE -> "device block"
            StanzaContext.NUMA -> "numa block"
            StanzaContext.HEADER -> "header block"
            StanzaContext.CHECK_RESTART -> "check_restart block"
            StanzaContext.EXPOSE -> "expose block"
            StanzaContext.TRANSPARENT_PROXY -> "transparent_proxy block"
            StanzaContext.MESH_GATEWAY -> "mesh_gateway block"
            StanzaContext.TARGET -> "target block"
            StanzaContext.WAIT -> "wait block"
            StanzaContext.STRATEGY -> "strategy block"
            StanzaContext.REGION -> "region block"
            StanzaContext.LISTENER -> "listener block"
            StanzaContext.TLS -> "tls block"
            StanzaContext.MESH -> "mesh block"
            StanzaContext.PATH -> "path block"
            StanzaContext.MOUNT_OPTIONS -> "mount_options block"
            StanzaContext.META -> "meta block"
            StanzaContext.CONSUL -> "consul block"
            StanzaContext.DISCONNECT -> "disconnect block"
            StanzaContext.CNI -> "cni block"
            StanzaContext.OPTIONS -> "options block"
            StanzaContext.HEADERS -> "headers block"
            StanzaContext.POLICY -> "policy block"
            StanzaContext.DISPATCH_PAYLOAD -> "dispatch_payload block"
            StanzaContext.CHANGE_SCRIPT -> "change_script block"
        }
    }
}

// Cache the regex pattern for performance
private val stanzaBlockPattern by lazy {
    Regex(NomadStanzaHierarchy.buildStanzaMatchPattern())
}