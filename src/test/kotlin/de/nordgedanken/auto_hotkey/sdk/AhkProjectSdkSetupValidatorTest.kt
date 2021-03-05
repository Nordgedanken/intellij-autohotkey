package de.nordgedanken.auto_hotkey.sdk

import com.intellij.json.JsonFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class AhkProjectSdkSetupValidatorTest : FunSpec({
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()

    test("isApplicableFor") {
        every { virtualFile.fileType } returns AhkFileType
        AhkProjectSdkSetupValidator.isApplicableFor(project, virtualFile) shouldBe true
        every { virtualFile.fileType } returns JsonFileType.INSTANCE
        AhkProjectSdkSetupValidator.isApplicableFor(project, virtualFile) shouldBe false
    }
})
