package de.nordgedanken.auto_hotkey.sdk

import com.google.common.flogger.FluentLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.ProjectRootManager
import com.jetbrains.rd.util.use
import de.nordgedanken.auto_hotkey.util.AhkBundle
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons
import org.jdom.Element
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit.SECONDS
import javax.swing.Icon

/**
 * Controls how the AutoHotkey Sdk type will look and work in the IDE. Registered in plugin.xml
 */
object AhkSdkType : SdkType("AutoHotkeySDK") {
    private val logger = FluentLogger.forEnclosingClass()

    fun getInstance() = findInstance(this::class.java)

    override fun getIcon(): Icon = AhkIcons.EXE

    override fun suggestHomePath() = """C:\Program Files\AutoHotkey"""

    /**
     * Verified that there is an "AutoHotkey.exe" in the directory the user selects
     */
    override fun isValidSdkHome(selectedSdkPath: String): Boolean {
        try {
            Files.walk(Paths.get(selectedSdkPath), 1).use { paths ->
                return paths.filter { path: Path -> Files.isRegularFile(path) }
                        .anyMatch { file: Path -> "AutoHotkey.exe" == file.fileName.toString() }
            }
        } catch (e: IOException) {
            logger.atSevere().withCause(e).log()
            return false
        }
    }

    override fun getInvalidHomeMessage(path: String): String = AhkBundle.msg("ahksdktype.invalidhome")

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String) = AhkConstants.LANGUAGE_NAME

    override fun getVersionString(sdkHome: String?): String? {
        sdkHome ?: return null
        val ahkExePath = File(sdkHome, "AutoHotkey.exe").absolutePath
        AhkSdkType::class.java.getResourceAsStream("/sdk/getAhkVersion.ahk").use { versionScriptInputStream ->
            File.createTempFile("getVersion", ".ahk").apply {
                versionScriptInputStream.copyTo(outputStream())
                deleteOnExit()
            }.run {
                ProcessBuilder(ahkExePath, absolutePath).redirectErrorStream(true).start().run {
                    val processSuccess = waitFor(3, SECONDS)
                    if (processSuccess) {
                        return inputStream.bufferedReader().readText().also {
                            check(!it.contains("\n")) { "The code to get the AutoHotkey version returned additional lines: $it" }
                        }
                    }
                }
            }
        }
        return "unknown version"
    }

    override fun createAdditionalDataConfigurable(sdkModel: SdkModel, sdkModificator: SdkModificator): AdditionalDataConfigurable? {
        return null
    }

    override fun getPresentableName() = "AutoHotkey SDK"

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        //do nothing for now
    }

    override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel) = true
}

//convenience method to get the projectSdk
val Project.sdk: Sdk? get() = ProjectRootManager.getInstance(this).projectSdk
