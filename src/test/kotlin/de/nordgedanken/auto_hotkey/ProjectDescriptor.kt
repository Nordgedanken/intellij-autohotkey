package de.nordgedanken.auto_hotkey

import com.intellij.testFramework.LightProjectDescriptor
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

/**
 * Allows us to set a specific [LightProjectDescriptor] for a specific test in a very compact fashion.
 * The [descriptor] class must be a kotlin object (`object Foo : LightProjectDescriptor`).
 *
 * Example values:
 * - [WithOneAhkSdk]
 *
 * @see AhkBasePlatformTestCase.getProjectDescriptor
 */
@Inherited
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProjectDescriptor(val descriptor: KClass<out LightProjectDescriptor>)
