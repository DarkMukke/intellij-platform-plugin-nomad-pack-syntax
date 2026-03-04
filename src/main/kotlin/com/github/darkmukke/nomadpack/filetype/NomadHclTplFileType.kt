package com.github.darkmukke.nomadpack.filetype

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * File type for .nomad.tpl files (Nomad templates with [[ ]] expressions)
 */
object NomadHclTplFileType : LanguageFileType(NomadTplLanguage) {
    override fun getName(): String = "Nomad HCL Template"
    override fun getDescription(): String = "Nomad Pack HCL template file with [[ ]] expressions"
    override fun getDefaultExtension(): String = "nomad.tpl"
    override fun getIcon(): Icon = NomadTplIcons.HCL_FILE
}
