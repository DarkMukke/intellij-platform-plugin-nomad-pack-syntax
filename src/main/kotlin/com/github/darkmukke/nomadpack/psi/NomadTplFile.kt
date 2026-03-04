package com.github.darkmukke.nomadpack.psi

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.github.darkmukke.nomadpack.filetype.NomadTplFileType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class NomadTplFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NomadTplLanguage) {
    override fun getFileType(): FileType = NomadTplFileType

    override fun toString(): String = "Nomad Template File"
}
