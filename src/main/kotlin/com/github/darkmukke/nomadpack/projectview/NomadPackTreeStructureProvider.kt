package com.github.darkmukke.nomadpack.projectview

import com.github.darkmukke.nomadpack.filetype.NomadTplIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager

/**
 * Detects Nomad Pack directories and decorates them with custom icons
 */
class NomadPackTreeStructureProvider : TreeStructureProvider {
    
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): Collection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children
        
        return children.map { node ->
            when {
                node is PsiDirectoryNode && isNomadPack(node.virtualFile) -> {
                    NomadPackDirectoryNode(project, node.value!!, settings)
                }
                node is PsiFileNode && node.virtualFile?.name == "metadata.hcl" -> {
                    DecoratedFileNode(project, node.value!!, settings, NomadTplIcons.METADATA)
                }
                node is PsiFileNode && node.virtualFile?.name == "variables.hcl" -> {
                    DecoratedFileNode(project, node.value!!, settings, NomadTplIcons.VARIABLES)
                }
                node is PsiDirectoryNode && node.virtualFile?.name == "templates" && isInNomadPack(node.virtualFile) -> {
                    DecoratedDirectoryNode(project, node.value!!, settings, NomadTplIcons.TEMPLATES_DIR)
                }
                else -> node
            }
        }
    }
    
    private fun isNomadPack(dir: VirtualFile?): Boolean {
        if (dir == null || !dir.isDirectory) return false
        
        val hasMetadata = dir.findChild("metadata.hcl") != null
        val hasVariables = dir.findChild("variables.hcl") != null
        
        return hasMetadata && hasVariables
    }
    
    private fun isInNomadPack(dir: VirtualFile?): Boolean {
        if (dir == null) return false
        
        var current = dir.parent
        while (current != null) {
            if (isNomadPack(current)) return true
            current = current.parent
        }
        
        return false
    }
}

class NomadPackDirectoryNode(
    project: Project,
    value: PsiDirectory,
    viewSettings: ViewSettings?
) : PsiDirectoryNode(project, value, viewSettings) {
    
    override fun updateImpl(data: PresentationData) {
        super.updateImpl(data)
        data.setIcon(NomadTplIcons.PACK)
    }
}

class DecoratedFileNode(
    project: Project,
    value: com.intellij.psi.PsiFile,
    viewSettings: ViewSettings?,
    private val customIcon: javax.swing.Icon
) : PsiFileNode(project, value, viewSettings) {
    
    override fun updateImpl(data: PresentationData) {
        super.updateImpl(data)
        data.setIcon(customIcon)
    }
}

class DecoratedDirectoryNode(
    project: Project,
    value: PsiDirectory,
    viewSettings: ViewSettings?,
    private val customIcon: javax.swing.Icon
) : PsiDirectoryNode(project, value, viewSettings) {
    
    override fun updateImpl(data: PresentationData) {
        super.updateImpl(data)
        data.setIcon(customIcon)
    }
}
