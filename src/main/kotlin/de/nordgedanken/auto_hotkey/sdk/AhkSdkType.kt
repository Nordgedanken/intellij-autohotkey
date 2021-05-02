package de.nordgedanken.auto_hotkey.sdk

import com.google.common.flogger.FluentLogger
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.SdkType
import com.jetbrains.rd.util.use
import de.nordgedanken.auto_hotkey.runconfig.model.AhkSwitch
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

const val GET_AHK_VERSION_V1 = """FileAppend %A_AhkVersion%, *"""
const val GET_AHK_VERSION_V2 = """FileAppend A_AhkVersion, "*""""

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

    /**
     * This method executes once while a new Sdk is being created. It will create a temporary file with the contents of
     * [GET_AHK_VERSION_V1] and then try to execute it with the Sdk being created. If it fails, it will try again with
     * [GET_AHK_VERSION_V2] (in case the user is trying to add a v2 Ahk sdk). If both fail, it simply returns "unknown
     * version" as the official version for this sdk.
     */
    override fun getVersionString(sdkHome: String?): String? {
        sdkHome ?: return null
        val ahkExePath = File(sdkHome, "AutoHotkey.exe").absolutePath
        createTempFile().apply {
            writeText(GET_AHK_VERSION_V1)
            deleteOnExit()
        }.runCatching {
            ProcessBuilder(ahkExePath, AhkSwitch.ERROR_STD_OUT.switchName, absolutePath).run {
                kotlin.runCatching {
                    return startProcessAndReturnSingleLineOutput()
                }.onFailure {
                    (this@runCatching).writeText(GET_AHK_VERSION_V2)
                    return this@run.startProcessAndReturnSingleLineOutput()
                }
            }
        }
        return "unknown version"
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator
    ): AdditionalDataConfigurable? {
        return null
    }

    override fun getPresentableName() = "AutoHotkey SDK"

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        // do nothing for now
    }

    override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel) = true
}

fun Sdk.isAhkSdk(): Boolean = sdkType is AhkSdkType

/**
 * Starts the process associated with this ProcessBuilder and verifies that it terminated successfully with no output to
 * stderr. Assuming both conditions pass, it will verify that only a single line was printed to stdout and then
 * subsequently return that line.
 *
 * Note: This method should only be executed within a runCatching block to handle potential exceptions being thrown
 */
private fun ProcessBuilder.startProcessAndReturnSingleLineOutput(): String = start().run {
    val processTerminated = waitFor(3, SECONDS)
    check(processTerminated && errorStream.available() == 0) { "Process failed to run correctly" }
    return inputStream.bufferedReader().readText().also {
        check(!it.contains("\n")) { "The process output contained multiple lines: $it" }
    }
}
