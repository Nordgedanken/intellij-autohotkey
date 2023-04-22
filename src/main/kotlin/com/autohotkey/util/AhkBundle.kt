package com.autohotkey.util

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.ResourceBundle

object AhkBundle {
    @NonNls
    internal const val BUNDLE_NAME = "localization.AhkBundle"
    private val BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME)

    /**
     * Retrieve property value from bundle file
     *
     * Ex: AhkBundle.msg("runconfig.configtab.scriptpath.label")
     */
    fun msg(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any?): String {
        return AbstractBundle.message(BUNDLE, key, *params)
    }
}
