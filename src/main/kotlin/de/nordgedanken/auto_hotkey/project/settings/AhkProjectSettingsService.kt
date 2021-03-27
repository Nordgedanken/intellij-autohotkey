package de.nordgedanken.auto_hotkey.project.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.JDOMExternalizerUtil
import de.nordgedanken.auto_hotkey.sdk.getAhkSdkByName
import de.nordgedanken.auto_hotkey.sdk.getAhkSdks
import org.jdom.Element

private const val serviceName: String = "AhkProjectSettings"

@Service
@State(
    name = serviceName,
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class AhkProjectSettingsService : PersistentStateComponent<Element> {
    var defaultAhkSdk: Sdk? = null

    override fun getState(): Element {
        println("getState called")
        val element = Element(serviceName)
        defaultAhkSdk?.let { JDOMExternalizerUtil.writeField(element, "defaultAhkSdk", it.name) }
        return element
    }

    override fun loadState(state: Element) {
        val res = JDOMExternalizerUtil.readField(state, "defaultAhkSdk")
        println("loadState Field read $res")
        defaultAhkSdk = getAhkSdkByName(res) ?: getAhkSdks().firstOrNull()
    }
}

var Project.defaultAhkSdk: Sdk?
    get() = service<AhkProjectSettingsService>().defaultAhkSdk
    set(newDefaultAhkSdk) {
        println("called to set '${newDefaultAhkSdk?.name}' as the new default sdk")
        service<AhkProjectSettingsService>().defaultAhkSdk = newDefaultAhkSdk
    }

