package de.nordgedanken.auto_hotkey.sdk

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk

/**
 * These methods are auto-written to a static class by Kotlin compiler.
 * Read here to learn more: https://proandroiddev.com/utils-class-in-kotlin-387a09b8d495
 */

/**
 * Will retrieve the Ahk Sdk that has the given name
 */
fun getAhkSdkByName(sdkName: String): Sdk? = getAhkSdks().find { it.name == sdkName }

fun getAhkSdks(): MutableList<Sdk> {
    return ProjectJdkTable.getInstance().getSdksOfType(
            ProjectJdkTable.getInstance().getSdkTypeByName(
                    AhkSdkType.findInstance(AhkSdkType::class.java).name)).toMutableList()
}
