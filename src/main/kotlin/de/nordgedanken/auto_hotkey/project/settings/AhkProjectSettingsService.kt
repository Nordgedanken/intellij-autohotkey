package de.nordgedanken.auto_hotkey.project.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.ProjectJdkTable.JDK_TABLE_TOPIC
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.JDOMExternalizerUtil
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import de.nordgedanken.auto_hotkey.sdk.getFirstAvailableAhkSdk
import de.nordgedanken.auto_hotkey.sdk.isAhkSdk
import org.jdom.Element

const val AHK_PROJECT_SETTINGS: String = "AhkProjectSettings"

/**
 * Manages project-level state. Currently, we only track the "default ahk sdk" which is used when generating run configs
 * from context.
 */
@Service
@State(name = AHK_PROJECT_SETTINGS)
class AhkProjectSettingsService(
    project: Project
) : PersistentStateComponent<Element> {
    /**
     * Set to internal on purpose to prevent unauthorized modification. It is set by default to the first available
     * ahk sdk, but that will be overridden upon reading any pre-saved default sdk in the user's storage.
     */
    internal var defaultAhkSdk: Sdk? = getFirstAvailableAhkSdk()

    init {
        project.messageBus.connect().subscribe(
            JDK_TABLE_TOPIC,
            object : ProjectJdkTable.Listener {
                override fun jdkAdded(jdk: Sdk) {
                    if (defaultAhkSdk === null && jdk.isAhkSdk()) defaultAhkSdk = jdk
                }

                /**
                 * Called right before the sdk is actually deleted from the ide's memory. Since it still exists within
                 * the ide's memory, we have to remove it before determining which ahk sdk should become the new default
                 */
                override fun jdkRemoved(jdk: Sdk) {
                    if (defaultAhkSdk === jdk) defaultAhkSdk = getAhkSdks().minus(jdk).firstOrNull()
                }

                override fun jdkNameChanged(jdk: Sdk, previousName: String) {}
            }
        )
    }

    override fun getState() = Element(AHK_PROJECT_SETTINGS).also { elem ->
        defaultAhkSdk?.let {
            JDOMExternalizerUtil.writeField(elem, AhkProjectSettingsService::defaultAhkSdk.name, it.name)
        }
    }

    override fun loadState(state: Element) {
        val res = JDOMExternalizerUtil.readField(state, AhkProjectSettingsService::defaultAhkSdk.name)
        getAhkSdkByName(res)?.let { defaultAhkSdk = it }
    }
}

/**
 * Convenience method to access the default ahk sdk for a project. This is the only way that the project's ahk sdk
 * should be accessed
 */
var Project.defaultAhkSdk: Sdk?
    get() = service<AhkProjectSettingsService>().defaultAhkSdk
    set(newDefaultAhkSdk) {
        service<AhkProjectSettingsService>().defaultAhkSdk = newDefaultAhkSdk
    }
