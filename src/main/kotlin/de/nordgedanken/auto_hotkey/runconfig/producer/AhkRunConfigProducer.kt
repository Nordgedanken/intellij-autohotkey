package de.nordgedanken.auto_hotkey.runconfig.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findDescendantOfType
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.lang.psi.AhkLine
import de.nordgedanken.auto_hotkey.openapiext.toPsiFile
import de.nordgedanken.auto_hotkey.runconfig.core.AhkRunConfig
import de.nordgedanken.auto_hotkey.runconfig.core.AhkRunConfigType
import de.nordgedanken.auto_hotkey.sdk.AhkSdkType
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.sdk

class AhkRunConfigProducer : LazyRunConfigurationProducer<AhkRunConfig>() {
    override fun getConfigurationFactory(): ConfigurationFactory {
        return AhkRunConfigType.getInstance().factory
    }

    /**
     * Fills in the provided configuration if the provided context meets the following conditions:
     * 1. The context's file is an AhkFile
     * 2. The file has at least one line of code in it
     *
     * The rules for selecting the runner are:
     * 1. If the project has an sdk:
     *   - if the sdk is an AhkSdk, use that.
     *   - if the sdk is some other sdk, get the ahk sdks configured in the project and take the 1st one
     *   - otherwise return null and move to #2
     * 2. If the project has no sdk, return an empty string and let the user configure it in the panel that opens.
     */
    override fun setupConfigurationFromContext(
        configuration: AhkRunConfig,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val location = context.location ?: return false
        val file = location.virtualFile ?: return false
        if (file.fileType !is AhkFileType) return false
        file.toPsiFile(location.project)?.findDescendantOfType<AhkLine>() ?: return false

        configuration.name = file.nameWithoutExtension
        configuration.runConfigSettings.runner = location.project.sdk?.run {
            if (this.sdkType is AhkSdkType) this else getAhkSdks().firstOrNull()
        }?.name ?: ""
        configuration.runConfigSettings.pathToScript = file.path
        return true
    }

    override fun isConfigurationFromContext(configuration: AhkRunConfig, context: ConfigurationContext): Boolean {
        val file = context.location?.virtualFile ?: return false
        if (file.fileType !is AhkFileType) return false
        configuration.runConfigSettings.run {
            return pathToScript == file.path && arguments.isEmpty()
        }
    }
}
