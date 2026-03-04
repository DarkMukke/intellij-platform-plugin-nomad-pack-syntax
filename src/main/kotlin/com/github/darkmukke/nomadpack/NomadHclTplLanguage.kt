package com.github.darkmukke.nomadpack

import com.intellij.lang.Language

object NomadHclTplLanguage : Language("NomadHclTpl") {
    private fun readResolve(): Any = NomadHclTplLanguage
    override fun getDisplayName(): String = "Nomad HCL Template"
}
