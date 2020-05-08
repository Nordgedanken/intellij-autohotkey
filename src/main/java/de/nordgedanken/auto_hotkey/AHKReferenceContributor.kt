package de.nordgedanken.auto_hotkey

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.LoggerRt
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext


class AHKReferenceContributor : PsiReferenceContributor() {
    private val log: Logger = Logger.getInstance("#de.nordgedanken.auto_hotkey.AHKReferenceContributor")
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement,
                                                        context: ProcessingContext): Array<PsiReference> {
                        val literalExpression: PsiLiteralExpression = element as PsiLiteralExpression
                        val value: String? = if (literalExpression.value is String) literalExpression.value as String? else null
                        //&& value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR)


                        log.debug("PsiReferenceProvider value: $value")
                        if (value != null) {
                            val property = TextRange(0, value.length + 1)
                            val reference = AHKReference(element, property)
                            log.debug("AHKReference: $reference")
                            return arrayOf(reference)
                        }
                        return PsiReference.EMPTY_ARRAY
                    }
                })
    }
}
