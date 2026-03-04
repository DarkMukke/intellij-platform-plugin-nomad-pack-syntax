package com.github.darkmukke.nomadpack.completion

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.github.darkmukke.nomadpack.lexer.NomadTplTokenTypes
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

/**
 * Provides autocompletion for Nomad stanzas and variables
 */
class NomadTplCompletionContributor : CompletionContributor() {

    init {
        // Context-aware completion for stanzas and properties
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(NomadTplLanguage),
            ContextAwareCompletionProvider()
        )

        // Trigger after a dot in template expressions for variables
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withLanguage(NomadTplLanguage)
                .afterLeaf(PlatformPatterns.psiElement(NomadTplTokenTypes.DOT)),
            VariableCompletionProvider()
        )
    }
}

/**
 * Provides completion for Nomad stanza keywords
 */
class NomadStanzaCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        // Add all Nomad keywords as completion items
        for (keyword in NomadTplTokenTypes.KEYWORDS) {
            if (keyword.length > 1 && !keyword.contains("-")) {  // Filter out operators
                result.addElement(
                    LookupElementBuilder.create(keyword)
                        .withIcon(AllIcons.Nodes.Property)
                        .withTypeText(getKeywordCategory(keyword))
                        .bold()
                )
            }
        }
    }

    private fun getKeywordCategory(keyword: String): String {
        return when (keyword) {
            "job" -> "Top-level stanza"
            "group" -> "TaskGroup stanza"
            "task" -> "Task stanza"
            "service" -> "Service stanza"
            "network" -> "Network stanza"
            "resources" -> "Resources stanza"
            "driver" -> "Task driver"
            "config" -> "Configuration"
            "env" -> "Environment"
            "vault" -> "Vault integration"
            "consul" -> "Consul integration"
            "constraint", "affinity", "spread" -> "Scheduling policy"
            "update", "migrate", "reschedule" -> "Update strategy"
            "check" -> "Health check"
            "port" -> "Network port"
            "volume", "volume_mount" -> "Storage"
            "template" -> "Template rendering"
            "artifact" -> "Artifact download"
            "restart" -> "Restart policy"
            "periodic" -> "Periodic job"
            "parameterized" -> "Parameterized job"
            else -> "Nomad stanza"
        }
    }
}

class VariableCompletionProvider : CompletionProvider<CompletionParameters>() {
    
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val file = parameters.originalFile.virtualFile ?: return
        
        // Find the pack root
        val packRoot = findPackRoot(file) ?: return
        
        // Parse variables.hcl
        val variablesFile = packRoot.findChild("variables.hcl") ?: return
        val variables = parseVariables(variablesFile)
        
        // Extract pack name from metadata.hcl if available
        val metadataFile = packRoot.findChild("metadata.hcl")
        val packName = if (metadataFile != null) {
            extractPackName(metadataFile) ?: packRoot.name
        } else {
            packRoot.name
        }
        
        // Add variable completions
        for (variable in variables) {
            result.addElement(
                LookupElementBuilder.create(".${packName}.${variable.name}")
                    .withIcon(AllIcons.Nodes.Variable)
                    .withTypeText(variable.type)
                    .withTailText(" from ${packName}/variables.hcl", true)
            )
        }
        
        // Add common built-in variables
        result.addElement(
            LookupElementBuilder.create(".nomad_pack.pack.name")
                .withIcon(AllIcons.Nodes.Variable)
                .withTypeText("string")
                .withTailText(" (built-in)", true)
        )
        
        result.addElement(
            LookupElementBuilder.create(".nomad_pack.pack.version")
                .withIcon(AllIcons.Nodes.Variable)
                .withTypeText("string")
                .withTailText(" (built-in)", true)
        )
    }
    
    private fun findPackRoot(file: VirtualFile): VirtualFile? {
        var current = file.parent
        
        while (current != null) {
            val hasMetadata = current.findChild("metadata.hcl") != null
            val hasVariables = current.findChild("variables.hcl") != null
            
            if (hasMetadata && hasVariables) {
                return current
            }
            
            current = current.parent
        }
        
        return null
    }
    
    private fun parseVariables(variablesFile: VirtualFile): List<VariableInfo> {
        val content = String(variablesFile.contentsToByteArray())
        val variables = mutableListOf<VariableInfo>()
        
        // Simple regex-based parsing of variable blocks
        val variablePattern = Regex("""variable\s+"([^"]+)"\s*\{([^}]*)\}""")
        val typePattern = Regex("""type\s*=\s*(\S+)""")
        
        variablePattern.findAll(content).forEach { match ->
            val name = match.groupValues[1]
            val body = match.groupValues[2]
            
            val typeMatch = typePattern.find(body)
            val type = typeMatch?.groupValues?.get(1) ?: "any"
            
            variables.add(VariableInfo(name, type))
        }
        
        return variables
    }
    
    private fun extractPackName(metadataFile: VirtualFile): String? {
        val content = String(metadataFile.contentsToByteArray())
        
        // Extract app name from metadata
        val appPattern = Regex("""app\s*\{[^}]*name\s*=\s*"([^"]+)"""")
        val match = appPattern.find(content)
        
        return match?.groupValues?.get(1)
    }
}

data class VariableInfo(val name: String, val type: String)
