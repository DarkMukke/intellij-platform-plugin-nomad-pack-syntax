package com.github.darkmukke.nomadpack.filetype

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object NomadTplFileType : LanguageFileType(NomadTplLanguage) {
    override fun getName(): String = "Nomad Template"
    override fun getDescription(): String = "Nomad Pack template file"
    override fun getDefaultExtension(): String = "tpl"
    override fun getIcon(): Icon = NomadTplIcons.FILE
}
