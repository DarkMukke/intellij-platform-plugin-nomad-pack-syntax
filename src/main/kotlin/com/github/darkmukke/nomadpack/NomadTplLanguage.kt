package com.github.darkmukke.nomadpack

import com.intellij.lang.Language

object NomadTplLanguage : Language("NomadTpl") {
    private fun readResolve(): Any = NomadTplLanguage
    override fun getDisplayName(): String = "Nomad Template"
}
