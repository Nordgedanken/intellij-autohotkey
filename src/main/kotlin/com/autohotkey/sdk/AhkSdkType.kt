package com.autohotkey.sdk

import com.autohotkey.runconfig.model.AhkSwitch
import com.autohotkey.util.AhkBundle
import com.autohotkey.util.AhkConstants
import com.autohotkey.util.AhkIcons
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFolderDescriptor
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.ui.SelectFromListDialog
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer
import com.intellij.util.io.isFile
import org.jdom.Element
import java.io.File
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Files.createTempFile
import java.nio.file.Paths
import java.util.concurrent.TimeUnit.SECONDS
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.ListSelectionModel
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

const val GET_AHK_VERSION_V1 = """FileAppend %A_AhkVersion%, *"""
const val GET_AHK_VERSION_V2 = """FileAppend A_AhkVersion, "*""""
const val AHK_DOCUMENTATION_FILENAME = "AutoHotkey.chm"
const val AHK_DOCUMENTATION_URL_V1 = "https://www.autohotkey.com"
const val AHK_DOCUMENTATION_URL_V2 = "https://lexikos.github.io/v2"

/**
 * Controls how the AutoHotkey Sdk type will look and work in the IDE. Registered in plugin.xml
 *
 * NOTE: If you need to pass an instance of this class to an IntelliJ API, you must call [getInstance]
 */
object AhkSdkType : SdkType("AutoHotkeySDK") {
    private val AHK_EXE_NAME_KEY = DataKey.create<String>("chosenAhkExeName")
    private val AHK_EXE_VERSION_KEY = DataKey.create<String>("chosenAhkExeVersion")
    private val versionPrefixRegex = Regex("""^\d+\.\d+[.-]\p{Alpha}?\d+""")

    /**
     * WARNING! You MUST call this method if any method in the JetBrains platform API requires an SdkType. Do NOT pass
     * the AhkSdkType object class directly - it will cause unexpected behavior!
     */
    fun getInstance() = findInstance(this::class.java)

    override fun getIcon(): Icon = AhkIcons.EXE

    override fun getPresentableName() = "${AhkConstants.LANGUAGE_NAME} SDK"

    override fun suggestHomePath() = """C:\Program Files\AutoHotkey"""

    /**
     * Returns a custom file descriptor that asks the user to select a folder for the AutoHotkey home path and then
     * select an executable within that directory to be the main executable associated with that sdk.
     */
    override fun getHomeChooserDescriptor(): FileChooserDescriptor {
        return object : FileChooserDescriptor(createSingleFolderDescriptor()) {
            override fun validateSelectedFiles(files: Array<VirtualFile>) {
                if (files.isNotEmpty()) {
                    val selectedPath = files[0].path
                    val exeFilesInSelectedPath = Files.walk(Paths.get(selectedPath), 1, FileVisitOption.FOLLOW_LINKS)
                        .filter { it.isRegularFile() }
                        .map { it.fileName.toString() }
                        .filter { it.lowercase().endsWith(".exe") }
                        .toList()
                    check(exeFilesInSelectedPath.isNotEmpty()) {
                        AhkBundle.msg("ahksdktype.createsdk.error.noexefound")
                    }
                    check(File(selectedPath).resolve(AHK_DOCUMENTATION_FILENAME).isFile) {
                        AhkBundle.msg("ahksdktype.createsdk.error.nochmfound")
                    }
                    val listDialog = SelectFromListDialog(
                        null,
                        exeFilesInSelectedPath.toTypedArray(),
                        { obj -> obj.toString() },
                        AhkBundle.msg("ahksdktype.createsdk.dialogexeselect.title"),
                        ListSelectionModel.SINGLE_SELECTION,
                    )

                    if (listDialog.showAndGet()) {
                        val selectedExe = listDialog.selection.single() as String
                        val versionStr = determineAhkVersionString("$selectedPath/$selectedExe")
                        checkNotNull(versionStr) { AhkBundle.msg("ahksdktype.createsdk.error.noversion") }
                        putUserData(AHK_EXE_NAME_KEY, selectedExe)
                        putUserData(AHK_EXE_VERSION_KEY, versionStr)
                    } else {
                        error(AhkBundle.msg("ahksdktype.createsdk.error.noexeselected"))
                    }
                }
            }
        }.apply {
            title = ProjectBundle.message("sdk.configure.home.title", presentableName)
            description = AhkBundle.msg("ahksdktype.createsdk.dialogselecthomepath.description")
        }
    }

    /**
     * Defaulting to true - we can ignore this method because we perform our own validation in
     * [getHomeChooserDescriptor] when the user is creating the AutoHotkey runner.
     */
    override fun isValidSdkHome(selectedSdkPath: String) = true

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String) = AhkConstants.LANGUAGE_NAME

    /**
     * This method executes once while a new Sdk is being created. It will create a temporary file with the contents of
     * [GET_AHK_VERSION_V1] and then try to execute it with the Sdk being created. If it fails, it will try again with
     * [GET_AHK_VERSION_V2] (in case the user is trying to add a v2 Ahk sdk). If both fail, it simply returns "unknown
     * version" as the official version for this sdk.
     */
    override fun getVersionString(sdk: Sdk): String? {
        return determineAhkVersionString(File(sdk.homePath, sdk.ahkExeName()).absolutePath)
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator,
    ): AdditionalDataConfigurable? {
        return null
    }

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        (additionalData as AhkSdkAdditionalData).writeTo(additional)
    }

    override fun loadAdditionalData(additional: Element) = AhkSdkAdditionalData.generateFrom(additional)

    override fun supportsCustomCreateUI() = true

    /**
     * Must override in order to show our custom FileDescriptor if the user decides to add an AutoHotkey runner through
     * the IntelliJ Community/Ultimate Project Structure window (rather than using the normal AutoHotkey Settings page).
     */
    override fun showCustomCreateUI(
        sdkModel: SdkModel,
        parentComponent: JComponent,
        selectedSdk: Sdk?,
        sdkCreatedCallback: Consumer<in Sdk>,
    ) {
        val newSdk = showUiToCreateNewAhkSdk()
        if (newSdk != null) {
            sdkCreatedCallback.consume(newSdk)
        }
    }

    fun showUiToCreateNewAhkSdk(): Sdk? {
        var newlyCreatedSdk: Sdk? = null
        val ahkFileChooser = homeChooserDescriptor
        FileChooser.chooseFile(ahkFileChooser, null, SdkConfigurationUtil.getSuggestedSdkRoot(this)) { chosenVFile ->
            val chosenExeName = ahkFileChooser.getUserData(AHK_EXE_NAME_KEY) as String
            val chosenExeVersion = ahkFileChooser.getUserData(AHK_EXE_VERSION_KEY) as String
            newlyCreatedSdk = SdkConfigurationUtil.createSdk(
                ProjectJdkTable.getInstance().allJdks.asList(),
                chosenVFile,
                this.getInstance(),
                AhkSdkAdditionalData(chosenExeName),
                generateAhkSdkNameBasedOn(chosenExeVersion),
            )
        }
        return newlyCreatedSdk
    }

    /**
     * Builds the sdk name as "AutoHotkey v<major>.<minor>[.-]<patch>" based on the version string obtained from the
     * executable
     */
    private fun generateAhkSdkNameBasedOn(ahkExeVersion: String): String {
        return "${AhkConstants.LANGUAGE_NAME} v${versionPrefixRegex.find(ahkExeVersion)?.value}"
    }

    /**
     * This method tries to get the version of the Ahk executable passed to it. It will create a temporary file with the
     * contents of [GET_AHK_VERSION_V1] and then try to execute it. If it fails or there isn't a single line of output
     * showing the version number, it will try again with [GET_AHK_VERSION_V2] (for when the user is trying to add a v2
     * Ahk sdk). If both fail, it simply returns null which can be used to handle custom logic in the calling method.
     */
    private fun determineAhkVersionString(fullPathToAhkExec: String): String? {
        val ahkExePath = File(fullPathToAhkExec).absolutePath
        createTempFile("", "").toFile().apply {
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
        return null
    }
}

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
