package de.nordgedanken.auto_hotkey.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import de.nordgedanken.auto_hotkey.psi.AHKTypes.INTEGER_LITERAL
import de.nordgedanken.auto_hotkey.psi.AHKTypes.STRING_LITERAL

interface RsComplexLiteral {
    val node: ASTNode
    val offsets: LiteralOffsets
}

interface AHKLiteralWithSuffix : RsComplexLiteral {
    val validSuffixes: List<String>
}

interface AHKTextLiteral {
    val value: String?
    val hasUnpairedQuotes: Boolean
}
sealed class AHKLiteralKind(val node: ASTNode) {
    class Boolean(node: ASTNode) : AHKLiteralKind(node) {
        val value: kotlin.Boolean = node.chars == "true"
    }

    class Integer(node: ASTNode) : AHKLiteralKind(node), AHKLiteralWithSuffix {
        override val offsets: LiteralOffsets by lazy { offsetsForNumber(node) }

        override val validSuffixes: List<kotlin.String> get() = emptyList()

        val value: Long? get() {
            val textValue = offsets.value?.substring(node.text) ?: return null
            val (start, radix) = when (textValue.take(2)) {
                "0x" -> 2 to 16
                else -> 0 to 10
            }
            val cleanTextValue = textValue.substring(start).filter { it != '_' }
            return try {
                java.lang.Long.parseLong(cleanTextValue, radix)
            } catch (e: NumberFormatException) {
                null
            }
        }
    }

    class String(node: ASTNode, val isByte: kotlin.Boolean) : AHKLiteralKind(node), AHKLiteralWithSuffix, AHKTextLiteral {
        override val offsets: LiteralOffsets by lazy { offsetsForText(node) }

        override val validSuffixes: List<kotlin.String> get() = emptyList()

        override val hasUnpairedQuotes: kotlin.Boolean
            get() = offsets.openDelim == null || offsets.closeDelim == null

        override val value: kotlin.String? get() {

            return offsets.value?.substring(node.text)
        }
    }

    companion object {
        fun fromAstNode(node: ASTNode): AHKLiteralKind? = when (node.elementType) {
            INTEGER_LITERAL -> Integer(node)

            STRING_LITERAL -> String(node, isByte = false)

            else -> null
        }
    }

}

fun offsetsForNumber(node: ASTNode): LiteralOffsets {
    val (start, digits) = when (node.text.take(2)) {
        "0x" -> 2 to "0123456789abcdefABCDEF"
        else -> 0 to "0123456789"
    }

    var hasExponent = false
    node.text.substring(start).forEachIndexed { i, ch ->
        if (!hasExponent && ch in "eE") {
            hasExponent = true
        } else if (ch !in digits && ch !in "+-_.") {
            return LiteralOffsets(
                    value = TextRange.create(0, i + start),
                    suffix = TextRange(i + start, node.textLength))
        }
    }

    return LiteralOffsets(value = TextRange.allOf(node.text))

}


private fun locatePrefix(node: ASTNode): Int {
    node.text.forEachIndexed { i, ch ->
        if (!ch.isLetter()) {
            return i
        }
    }
    return node.textLength
}


private inline fun doLocate(node: ASTNode, start: Int, locator: (Int) -> Int): Int =
        if (start >= node.textLength) start else locator(start)

fun offsetsForText(node: ASTNode): LiteralOffsets {

    val text = node.text
    val quote = when (node.elementType) {
        else -> '"'
    }

    val prefixEnd = locatePrefix(node)

    val openDelimEnd = doLocate(node, prefixEnd) {
        assert(text[it] == quote) { "expected open delimiter `$quote` but found `${text[it]}`" }
        it + 1
    }

    val valueEnd = doLocate(node, openDelimEnd, fun(start: Int): Int {
        var escape = false
        text.substring(start).forEachIndexed { i, ch ->
            if (escape) {
                escape = false
            } else when (ch) {
                '\\' -> escape = true
                quote -> return i + start
            }
        }
        return node.textLength
    })

    val closeDelimEnd = doLocate(node, valueEnd) {
        assert(text[it] == quote) { "expected close delimiter `$quote` but found `${text[it]}`" }
        it + 1
    }

    return LiteralOffsets.fromEndOffsets(prefixEnd, openDelimEnd, valueEnd, closeDelimEnd, node.textLength)
}

val AHKLitExpr.kind: AHKLiteralKind? get() {
    val literalAstNode = this.node.findChildByType(AHK_LITERALS) ?: return null
    return AHKLiteralKind.fromAstNode(literalAstNode)
            ?: error("Unknown literal: $literalAstNode (`$text`)")
}
