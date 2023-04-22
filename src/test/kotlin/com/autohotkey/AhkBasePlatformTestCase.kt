package com.autohotkey

import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import util.findAnnotationInstance

/**
 * All Ahk tests that require running on the Base Platform should extend this
 * class. This provides convenience methods to avoid duplication in subclasses.
 */
abstract class AhkBasePlatformTestCase : BasePlatformTestCase() {
    /**
     * Looks for the @ProjectDescriptor annotation on a test method (or its
     * parent class) to determine what kind of ProjectDescriptor to use in the
     * test setup. If none found, it will use a default empty Descriptor.
     */
    override fun getProjectDescriptor(): LightProjectDescriptor {
        val annotation = findAnnotationInstance<ProjectDescriptor>() ?: return EmptyDescriptor
        return annotation.descriptor.objectInstance
            ?: error("Only Kotlin objects defined with `object` keyword are allowed")
    }
}
