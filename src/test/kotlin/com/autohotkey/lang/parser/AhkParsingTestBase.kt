/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.autohotkey.lang.parser

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.ParsingTestCase
import com.autohotkey.AhkTestCase
import org.jetbrains.annotations.NonNls

abstract class AhkParsingTestBase(@NonNls dataPath: String) :
    ParsingTestCase(
        "com/autohotkey/lang/parser/$dataPath",
        "ahk",
        true,
        AhkParserDefinition()
    ),
    AhkTestCase {
    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return AhkTestCase.camelOrWordsToSnake(camelCase)
    }

    override fun getTestDataPath(): String = "src/test/resources"

    protected fun hasError(file: PsiFile): Boolean {
        var hasErrors = false
        file.accept(
            object : PsiElementVisitor() {
                override fun visitElement(element: PsiElement) {
                    if (element is PsiErrorElement) {
                        hasErrors = true
                        return
                    }
                    element.acceptChildren(this)
                }
            }
        )
        return hasErrors
    }

    override fun skipSpaces() = false

    override fun includeRanges() = true
}
