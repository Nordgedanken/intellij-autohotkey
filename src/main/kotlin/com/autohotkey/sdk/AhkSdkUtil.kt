package com.autohotkey.sdk

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkType

val AhkSdkTypeInstance: AhkSdkType get() = SdkType.findInstance(AhkSdkType::class.java)

/**
 * Gets the Ahk Sdk that has the given name if it exists. Otherwise, returns null.
 */
fun getAhkSdkByName(sdkName: String?): Sdk? = getAhkSdks().find { it.name == sdkName }

fun getAhkSdks() = ProjectJdkTable.getInstance().getSdksOfType(AhkSdkTypeInstance).toList()

fun getFirstAvailableAhkSdk(): Sdk? = getAhkSdks().firstOrNull()

fun Sdk.isAhkSdk(): Boolean = sdkType is AhkSdkType

fun Sdk.ahkExeName(): String {
    sdkAdditionalData ?: WriteAction.run<Throwable> {
        sdkModificator.run {
            sdkAdditionalData = AhkSdkAdditionalData()
            commitChanges()
        }
    }
    return (sdkAdditionalData as AhkSdkAdditionalData).exeName
}

val Sdk.ahkDocUrlBase: String get() = AhkSdkTypeInstance.getDefaultDocumentationUrl(this)
