package com.github.darkmukke.nomadpack.filetype

import com.intellij.ui.IconManager
import javax.swing.Icon

object NomadTplIcons {
    @JvmField
    val FILE: Icon = IconManager.getInstance().getIcon("/icons/nomadTpl.svg", NomadTplIcons::class.java)
    
    @JvmField
    val HCL_FILE: Icon = IconManager.getInstance().getIcon("/icons/nomadHclTpl.svg", NomadTplIcons::class.java)
    
    @JvmField
    val PACK: Icon = IconManager.getInstance().getIcon("/icons/nomadPack.svg", NomadTplIcons::class.java)
    
    @JvmField
    val METADATA: Icon = IconManager.getInstance().getIcon("/icons/metadata.svg", NomadTplIcons::class.java)
    
    @JvmField
    val VARIABLES: Icon = IconManager.getInstance().getIcon("/icons/variables.svg", NomadTplIcons::class.java)
    
    @JvmField
    val TEMPLATES_DIR: Icon = IconManager.getInstance().getIcon("/icons/templatesDir.svg", NomadTplIcons::class.java)
}
