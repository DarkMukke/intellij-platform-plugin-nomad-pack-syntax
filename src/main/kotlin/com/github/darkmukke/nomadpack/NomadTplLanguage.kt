package com.github.darkmukke.nomadpack

import com.github.darkmukke.nomadpack.filetype.NomadTplFileType
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType

object NomadTplLanguage : Language("NomadTpl") {
    private fun readResolve(): Any = NomadTplLanguage
    override fun getDisplayName(): String = "Nomad Template"
    override fun getAssociatedFileType(): LanguageFileType = NomadTplFileType
}
