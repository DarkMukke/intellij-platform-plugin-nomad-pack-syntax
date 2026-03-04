package com.github.darkmukke.nomadpack.filetype

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * File type for .nomad files (Nomad job specifications with template syntax)
 */
object NomadFileType : LanguageFileType(NomadTplLanguage) {
    override fun getName(): String = "Nomad"
    override fun getDescription(): String = "Nomad job specification file"
    override fun getDefaultExtension(): String = "nomad"
    override fun getIcon(): Icon = NomadTplIcons.HCL_FILE
}
