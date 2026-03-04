package com.github.darkmukke.nomadpack.project

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Detects if a project is a Nomad Pack Registry
 *
 * A Nomad Pack Registry typically has:
 * - README.md or readme.md
 * - LICENSE or LICENSE.md or similar
 * - packs/ directory containing individual pack folders
 */
object NomadPackRegistryDetector {

    /**
     * Check if the given project is a Nomad Pack Registry
     */
    fun isNomadPackRegistry(project: Project): Boolean {
        val baseDir = project.baseDir ?: return false
        return isNomadPackRegistry(baseDir)
    }

    /**
     * Check if the given directory is a Nomad Pack Registry root
     */
    fun isNomadPackRegistry(dir: VirtualFile): Boolean {
        if (!dir.isDirectory) return false

        // Check for packs/ directory
        val packsDir = dir.findChild("packs") ?: return false
        if (!packsDir.isDirectory) return false

        // Check for README (case-insensitive)
        val hasReadme = dir.children.any {
            it.name.lowercase().startsWith("readme")
        }

        // Check for LICENSE file (case-insensitive)
        val hasLicense = dir.children.any {
            it.name.lowercase().startsWith("license")
        }

        // Optional: Check if packs/ contains at least one valid pack
        val hasValidPacks = packsDir.children.any { packDir ->
            packDir.isDirectory && isNomadPack(packDir)
        }

        // Registry is valid if it has README, LICENSE, and packs/ directory
        // Or if it has packs/ with at least one valid pack (more lenient)
        return (hasReadme && hasLicense && packsDir.exists) || hasValidPacks
    }

    /**
     * Check if a directory is a Nomad Pack (has metadata.hcl and variables.hcl)
     */
    private fun isNomadPack(dir: VirtualFile): Boolean {
        if (!dir.isDirectory) return false

        val hasMetadata = dir.findChild("metadata.hcl") != null
        val hasVariables = dir.findChild("variables.hcl") != null

        return hasMetadata && hasVariables
    }

    /**
     * Find the registry root by walking up from the given directory
     */
    fun findRegistryRoot(startDir: VirtualFile?): VirtualFile? {
        var current = startDir
        var depth = 0

        while (current != null && depth < 10) {
            if (isNomadPackRegistry(current)) {
                return current
            }
            current = current.parent
            depth++
        }

        return null
    }
}
