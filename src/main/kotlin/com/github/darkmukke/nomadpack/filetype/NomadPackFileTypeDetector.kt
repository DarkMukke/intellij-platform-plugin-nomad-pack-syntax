package com.github.darkmukke.nomadpack.filetype

import com.github.darkmukke.nomadpack.project.NomadPackRegistryDetector
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.util.io.ByteSequence
import com.intellij.openapi.vfs.VirtualFile

/**
 * Detects if a .tpl file is part of a Nomad Pack Registry or Nomad Pack
 * and should use NomadTpl language.
 *
 * This helps distinguish Nomad Pack templates from other template types (Smarty, Twig, etc.)
 *
 * Detection strategy:
 * 1. Check if file is in a Nomad Pack Registry (README + LICENSE + packs/ directory)
 *    - If yes, ALL .tpl files in the project use NomadTpl
 * 2. Check if file is in a Nomad Pack (metadata.hcl + variables.hcl)
 *    - If yes, .tpl files in that pack use NomadTpl
 */
class NomadPackFileTypeDetector : FileTypeRegistry.FileTypeDetector {

    override fun detect(file: VirtualFile, firstBytes: ByteSequence, firstCharsIfText: CharSequence?): FileType? {
        // Only handle .tpl files
        if (file.extension != "tpl") return null

        // Strategy 1: Check if we're in a Nomad Pack Registry
        // This takes precedence - if the project is a registry, ALL .tpl files are Nomad templates
        val registryRoot = NomadPackRegistryDetector.findRegistryRoot(file.parent)
        if (registryRoot != null) {
            return NomadTplFileType
        }

        // Strategy 2: Check if this file is inside a Nomad Pack structure
        if (isInNomadPack(file)) {
            return NomadTplFileType
        }

        return null
    }

    override fun getVersion(): Int = 2

    private fun isInNomadPack(file: VirtualFile): Boolean {
        var current: VirtualFile? = file.parent

        // Walk up the directory tree looking for Nomad Pack markers
        var depth = 0
        while (current != null && depth < 10) {
            // Check if this directory has both metadata.hcl and variables.hcl
            val hasMetadata = current.findChild("metadata.hcl") != null
            val hasVariables = current.findChild("variables.hcl") != null

            if (hasMetadata && hasVariables) {
                return true
            }

            // Also check if we're in a templates/ directory of a pack
            if (current.name == "templates") {
                val parent = current.parent
                if (parent != null) {
                    val hasMetadataInParent = parent.findChild("metadata.hcl") != null
                    val hasVariablesInParent = parent.findChild("variables.hcl") != null
                    if (hasMetadataInParent && hasVariablesInParent) {
                        return true
                    }
                }
            }

            current = current.parent
            depth++
        }

        return false
    }
}
