package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.lexer.Lexer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.psi.AHKLitExpr
import de.nordgedanken.auto_hotkey.psi.AHKLiteralKind
import de.nordgedanken.auto_hotkey.psi.AHKTypes.STRING_LITERAL
import de.nordgedanken.auto_hotkey.psi.AHK_ALL_STRING_LITERALS
import de.nordgedanken.auto_hotkey.psi.impl.AHKExprImpl
import de.nordgedanken.auto_hotkey.psi.kind
import de.nordgedanken.auto_hotkey.stubs.AHKLitExprStub
import de.nordgedanken.auto_hotkey.stubs.AHKPlaceholderStub
import de.nordgedanken.auto_hotkey.stubs.AHKStubLiteralKind
import de.nordgedanken.auto_hotkey.types.ty.TyInteger
import org.intellij.lang.regexp.DefaultRegExpPropertiesProvider
import org.intellij.lang.regexp.RegExpLanguageHost
import org.intellij.lang.regexp.psi.RegExpChar
import org.intellij.lang.regexp.psi.RegExpGroup
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef
import java.lang.StringBuilder
import de.nordgedanken.auto_hotkey.lexer.AHKEscapesLexer

val AHKLitExpr.stubKind: AHKStubLiteralKind?
    get() {
        val stub = (greenStub as? AHKLitExprStub)
        if (stub != null) return stub.kind
        return when (val kind = kind) {
            is AHKLiteralKind.String -> AHKStubLiteralKind.String(kind.value, kind.isByte)
            is AHKLiteralKind.Integer -> AHKStubLiteralKind.Integer(kind.value, TyInteger.fromSuffixedLiteral(integerLiteral!!))
            else -> null
        }
    }

abstract class AHKLitExprMixin : AHKExprImpl, AHKLitExpr, RegExpLanguageHost {

    constructor(node: ASTNode) : super(node)
    constructor(stub: AHKPlaceholderStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun isValidHost(): Boolean =
            node.findChildByType(AHK_ALL_STRING_LITERALS) != null

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val valueNode = node.firstChildNode
        assert(valueNode is LeafElement)
        (valueNode as LeafElement).replaceWithText(text)
        return this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<AHKLitExpr> =
            escaperForLiteral(this)

    override fun getReferences(): Array<PsiReference> =
            PsiReferenceService.getService().getContributedReferences(this)

    override fun characterNeedsEscaping(c: Char): Boolean = false
    override fun supportsPerl5EmbeddedComments(): Boolean = false
    override fun supportsPossessiveQuantifiers(): Boolean = true
    override fun supportsPythonConditionalRefs(): Boolean = false
    override fun supportsNamedGroupSyntax(group: RegExpGroup): Boolean = true

    override fun supportsNamedGroupRefSyntax(ref: RegExpNamedGroupRef): Boolean =
            ref.isNamedGroupRef

    override fun supportsExtendedHexCharacter(regExpChar: RegExpChar): Boolean = true

    override fun isValidCategory(category: String): Boolean =
            DefaultRegExpPropertiesProvider.getInstance().isValidCategory(category)

    override fun getAllKnownProperties(): Array<Array<String>> =
            DefaultRegExpPropertiesProvider.getInstance().allKnownProperties

    override fun getPropertyDescription(name: String?): String? =
            DefaultRegExpPropertiesProvider.getInstance().getPropertyDescription(name)

    override fun getKnownCharacterClasses(): Array<Array<String>> =
            DefaultRegExpPropertiesProvider.getInstance().knownCharacterClasses
}

/** See [com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper] */
abstract class LiteralTextEscaperBase<T : PsiLanguageInjectionHost>(host: T) : LiteralTextEscaper<T>(host) {

    private var outSourceOffsets: IntArray? = null

    override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        val subText = rangeInsideHost.substring(myHost.text)
        val (offsets, result) = parseStringCharacters(subText, outChars)
        outSourceOffsets = offsets
        return result
    }

    override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
        val outSourceOffsets = outSourceOffsets!!
        val result = if (offsetInDecoded < outSourceOffsets.size) outSourceOffsets[offsetInDecoded] else -1
        return if (result == -1) {
            -1
        } else {
            (if (result <= rangeInsideHost.length) result else rangeInsideHost.length) + rangeInsideHost.startOffset
        }
    }

    protected abstract fun parseStringCharacters(chars: String, outChars: StringBuilder): Pair<IntArray, Boolean>
}

fun parseRustStringCharacters(chars: String): Triple<StringBuilder, IntArray, Boolean> {
    val outChars = StringBuilder()
    val (offsets, success) = parseRustStringCharacters(chars, outChars)
    return Triple(outChars, offsets, success)
}

fun parseRustStringCharacters(chars: String, outChars: StringBuilder): Pair<IntArray, Boolean> {
    val sourceOffsets = IntArray(chars.length + 1)
    val result = parseRustStringCharacters(chars, outChars, sourceOffsets)
    return sourceOffsets to result
}

private fun parseRustStringCharacters(chars: String, outChars: StringBuilder, sourceOffsets: IntArray): Boolean {
    return parseStringCharacters(AHKEscapesLexer.dummy(), chars, outChars, sourceOffsets, ::decodeEscape)
}
private fun decodeEscape(esc: String): String = when (esc) {
    "\\n" -> "\n"
    "\\r" -> "\r"
    "\\t" -> "\t"
    "\\0" -> "\u0000"
    "\\\\" -> "\\"
    "\\'" -> "\'"
    "\\\"" -> "\""

    else -> {
        assert(esc.length >= 2)
        assert(esc[0] == '\\')
        when (esc[1]) {
            'x' -> Integer.parseInt(esc.substring(2), 16).toChar().toString()
            'u' -> Integer.parseInt(esc.substring(3, esc.length - 1).filter { it != '_' }, 16).toChar().toString()
            '\r', '\n' -> ""
            else -> error("unreachable")
        }
    }
}

/**
 * Mimics [com.intellij.codeInsight.CodeInsightUtilCore.parseStringCharacters]
 * but obeys specific escaping rules provided by [decoder].
 */
inline fun parseStringCharacters(
        lexer: Lexer,
        chars: String,
        outChars: StringBuilder,
        sourceOffsets: IntArray,
        decoder: (String) -> String
): Boolean {
    val outOffset = outChars.length
    var index = 0
    for ((type, text) in chars.tokenize(lexer)) {
        // Set offset for the decoded character to the beginning of the escape sequence.
        sourceOffsets[outChars.length - outOffset] = index
        sourceOffsets[outChars.length - outOffset + 1] = index + 1
        when (type) {
            StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN -> {
                outChars.append(decoder(text))
                // And perform a "jump"
                index += text.length
            }

            StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN,
            StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN ->
                return false

            else -> {
                val first = outChars.length - outOffset
                outChars.append(text)
                val last = outChars.length - outOffset - 1
                // Set offsets for each character of given chunk
                for (i in first..last) {
                    sourceOffsets[i] = index
                    index++
                }
            }
        }
    }

    sourceOffsets[outChars.length - outOffset] = index

    return true
}


private class AHKNormalStringLiteralEscaper(host: AHKLitExpr) : LiteralTextEscaperBase<AHKLitExpr>(host) {
    override fun parseStringCharacters(chars: String, outChars: java.lang.StringBuilder): Pair<IntArray, Boolean> =
            parseRustStringCharacters(chars, outChars)

    override fun isOneLine(): Boolean = false
}

fun escaperForLiteral(lit: AHKLitExpr): LiteralTextEscaper<AHKLitExpr> {
    val elementType = lit.node.findChildByType(AHK_ALL_STRING_LITERALS)?.elementType
    assert(elementType == STRING_LITERAL) {
        "`${lit.text}` is not a string literal"
    }
    return AHKNormalStringLiteralEscaper(lit)
}

fun CharSequence.tokenize(lexer: Lexer): Sequence<Pair<IElementType, String>> =
        generateSequence({
            lexer.start(this)
            lexer.tokenType?.to(lexer.tokenText)
        }, {
            lexer.advance()
            lexer.tokenType?.to(lexer.tokenText)
        })
