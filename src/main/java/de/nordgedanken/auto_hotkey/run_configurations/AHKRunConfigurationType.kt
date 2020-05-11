package de.nordgedanken.auto_hotkey.run_configurations

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.util.ArrayUtil
import com.intellij.util.text.nullize
import de.nordgedanken.auto_hotkey.AHKIcons
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

private val EMPTY_FACTORIES = arrayOf<ConfigurationFactory>()

abstract class ConfigurationTypeBase protected constructor(@NonNls private val id: String,
                                                           @Nls private val displayName: String,
                                                           @Nls private val description: String? = null,
                                                           private val icon: NotNullLazyValue<Icon>?) : ConfigurationType {
    constructor(id: String, displayName: String, description: String?, icon: Icon?)
            : this(id, displayName, description, icon?.let { NotNullLazyValue.createConstantValue(it) })

    private var factories = EMPTY_FACTORIES

    protected fun addFactory(factory: ConfigurationFactory) {
        factories = ArrayUtil.append(factories, factory)
    }

    override fun getDisplayName() = displayName

    final override fun getConfigurationTypeDescription() = description.nullize() ?: displayName

    /** DO NOT override (not sealed because of backward compatibility) */
    override fun getIcon() = icon?.value

    final override fun getId() = id

    /** DO NOT override (not sealed because of backward compatibility) */
    override fun getConfigurationFactories() = factories
}


class AHKRunConfigurationType : ConfigurationTypeBase(
        "CargoCommandRunConfiguration",
        "Cargo Command",
        "Cargo command run configuration",
        AHKIcons.FILE
) {
    init {
        addFactory(AHKConfigurationFactory(this))
    }

    val factory: ConfigurationFactory get() = configurationFactories.single()

    companion object {
        fun getInstance(): AHKRunConfigurationType =
                ConfigurationTypeUtil.findConfigurationType(AHKRunConfigurationType::class.java)
    }
}
