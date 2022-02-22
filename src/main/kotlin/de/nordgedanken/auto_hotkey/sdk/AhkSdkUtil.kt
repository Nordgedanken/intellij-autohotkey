package de.nordgedanken.auto_hotkey.sdk

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk

/**
 * These methods are auto-written to a static class by Kotlin compiler.
 * Read here to learn more: https://proandroiddev.com/utils-class-in-kotlin-387a09b8d495
 */

/**
 * Gets the Ahk Sdk that has the given name if it exists. Otherwise returns null.
 */
fun getAhkSdkByName(sdkName: String?): Sdk? = getAhkSdks().find { it.name == sdkName }

fun getAhkSdks() = ProjectJdkTable.getInstance().getSdksOfType(AhkSdkType.getInstance()).toList()

fun getFirstAvailableAhkSdk(): Sdk? = getAhkSdks().firstOrNull()

fun Sdk.isAhkSdk(): Boolean = sdkType is AhkSdkType

fun Sdk.ahkExeName(): String {
    sdkAdditionalData ?: sdkModificator.run { sdkAdditionalData = AhkSdkAdditionalData(); commitChanges() }
    return (sdkAdditionalData as AhkSdkAdditionalData).exeName
}

val Sdk.ahkDocUrlBase: String get() =
    if (versionString?.startsWith("1") != false) AHK_DOCUMENTATION_URL_V1 else AHK_DOCUMENTATION_URL_V2
