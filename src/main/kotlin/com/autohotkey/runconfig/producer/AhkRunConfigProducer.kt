package com.autohotkey.runconfig.producer

import com.autohotkey.lang.core.isAhkFile
import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.runconfig.core.AhkRunConfig
import com.autohotkey.runconfig.core.AhkRunConfigType
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement

/**
 * Allows the user to generate a run config and run an ahk file by right-clicking on the ahk file within the project
 * file tree.
 */
class AhkRunConfigProducer : LazyRunConfigurationProducer<AhkRunConfig>() {
    override fun getConfigurationFactory(): ConfigurationFactory {
        return AhkRunConfigType.getInstance().factory
    }

    /**
     * Fills in the provided configuration if the provided context's file is an AhkFile
     *
     * The rules for selecting the runner are:
     * 1. If the project has a default ahk sdk, use that.
     * 2. If not, return an empty string and let the user configure it in the panel that opens.
     */
    override fun setupConfigurationFromContext(
        configuration: AhkRunConfig,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>,
    ): Boolean {
        val location = context.location ?: return false
        val file = location.virtualFile ?: return false
        if (!file.isAhkFile()) return false

        configuration.name = file.nameWithoutExtension
        configuration.runConfigSettings.runner = location.project.defaultAhkSdk?.name ?: ""
        configuration.runConfigSettings.pathToScript = file.path
        return true
    }

    override fun isConfigurationFromContext(configuration: AhkRunConfig, context: ConfigurationContext): Boolean {
        val file = context.location?.virtualFile ?: return false
        if (!file.isAhkFile()) return false
        configuration.runConfigSettings.run {
            return pathToScript == file.path && arguments.isEmpty()
        }
    }
}
