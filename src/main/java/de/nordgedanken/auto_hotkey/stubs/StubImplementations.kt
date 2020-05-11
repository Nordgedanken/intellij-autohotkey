package de.nordgedanken.auto_hotkey.stubs

import com.intellij.lang.*
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.impl.source.tree.LazyParseableElement
import com.intellij.psi.impl.source.tree.RecursiveTreeElementWalkingVisitor
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.impl.source.tree.TreeUtil
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.*
import com.intellij.util.BitUtil
import com.intellij.util.CharTable
import com.intellij.util.diff.FlyweightCapableTreeStructure
import com.intellij.util.io.DataInputOutputUtil
import de.nordgedanken.auto_hotkey.AHKLanguage
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.parser.AHKParser
import de.nordgedanken.auto_hotkey.psi.*
import de.nordgedanken.auto_hotkey.psi.AHKTypes.*
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement
import de.nordgedanken.auto_hotkey.psi.ext.stubKind
import de.nordgedanken.auto_hotkey.psi.impl.AHKBlockExprImpl
import de.nordgedanken.auto_hotkey.psi.impl.AHKBlockImpl
import de.nordgedanken.auto_hotkey.psi.impl.AHKLitExprImpl
import de.nordgedanken.auto_hotkey.psi.impl.AHKRetExprImpl
import de.nordgedanken.auto_hotkey.types.ty.TyInteger

fun factory(name: String): AHKStubElementType<*, *> = when (name) {
    "FUNCTION" -> AHKFunctionStub.Type
    "BLOCK" -> AHKBlockStubType
    "BLOCK_EXPR" -> AHKBlockExprStub.Type
    "RET_EXPR" -> AHKExprStubType("RET_EXPR", ::AHKRetExprImpl)
    "LIT_EXPR" -> AHKLitExprStub.Type
    else -> error("Unknown element $name")
}

class AHKLitExprStub(
        parent: StubElement<*>?, elementType: IStubElementType<*, *>,
        val kind: AHKStubLiteralKind?
) : AHKPlaceholderStub(parent, elementType) {
    object Type : AHKStubElementType<AHKLitExprStub, AHKLitExpr>("LIT_EXPR") {

        override fun shouldCreateStub(node: ASTNode): Boolean = shouldCreateExprStub(node)

        override fun serialize(stub: AHKLitExprStub, dataStream: StubOutputStream) {
            stub.kind.serialize(dataStream)
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): AHKLitExprStub =
                AHKLitExprStub(parentStub, this, AHKStubLiteralKind.deserialize(dataStream))

        override fun createStub(psi: AHKLitExpr, parentStub: StubElement<*>?): AHKLitExprStub =
                AHKLitExprStub(parentStub, this, psi.stubKind)

        override fun createPsi(stub: AHKLitExprStub): AHKLitExpr = AHKLitExprImpl(stub, this)
    }
}

sealed class AHKStubLiteralKind(val kindOrdinal: Int) {
    class String(val value: kotlin.String?, val isByte: kotlin.Boolean) : AHKStubLiteralKind(0)
    class Integer(val value: Long?, val ty: TyInteger?) : AHKStubLiteralKind(1)

    companion object {
        fun deserialize(dataStream: StubInputStream): AHKStubLiteralKind? {
            with(dataStream) {
                return when (readByte().toInt()) {
                    0 -> String(readUTFFastAsNullable(), readBoolean())
                    1 -> Integer(readLongAsNullable(), TyInteger.VALUES.getOrNull(readByte().toInt()))
                    else -> null
                }
            }
        }
    }
}

private fun AHKStubLiteralKind?.serialize(dataStream: StubOutputStream) {
    if (this == null) {
        dataStream.writeByte(-1)
        return
    }
    dataStream.writeByte(kindOrdinal)
    when (this) {
        is AHKStubLiteralKind.String -> {
            dataStream.writeUTFFastAsNullable(value)
            dataStream.writeBoolean(isByte)
        }
        is AHKStubLiteralKind.Integer -> {
            dataStream.writeLongAsNullable(value)
            dataStream.writeByte(ty?.ordinal ?: -1)
        }
    }
}

private fun StubInputStream.readLongAsNullable(): Long? = DataInputOutputUtil.readNullable(this, this::readLong)
private fun StubInputStream.readUTFFastAsNullable(): String? = DataInputOutputUtil.readNullable(this, this::readUTFFast)
private fun StubOutputStream.writeUTFFastAsNullable(value: String?) = DataInputOutputUtil.writeNullable(this, value, this::writeUTFFast)
private fun StubOutputStream.writeLongAsNullable(value: Long?) = DataInputOutputUtil.writeNullable(this, value, this::writeLong)

class AHKExprStubType<PsiT : AHKElement>(
        debugName: String,
        psiCtor: (AHKPlaceholderStub, IStubElementType<*, *>) -> PsiT
) : AHKPlaceholderStub.Type<PsiT>(debugName, psiCtor) {
    override fun shouldCreateStub(node: ASTNode): Boolean = shouldCreateExprStub(node)
}

val ASTNode.ancestors: Sequence<ASTNode>
    get() = generateSequence(this) {
        if (it is FileASTNode) null else it.treeParent
    }

private fun shouldCreateExprStub(node: ASTNode): Boolean {
    val element = node.ancestors.firstOrNull {
        val parent = it.treeParent
        parent?.elementType in AHK_ITEMS || parent is FileASTNode
    }
    return element != null && !element.isFunctionBody() && createStubIfParentIsStub(node)
}

private fun ASTNode.isFunctionBody() = this.elementType == BLOCK && treeParent?.elementType == FUNCTION

class AHKBlockExprStub(
        parent: StubElement<*>?, elementType: IStubElementType<*, *>,
        private val flags: Int
) : AHKPlaceholderStub(parent, elementType) {

    object Type : AHKStubElementType<AHKBlockExprStub, AHKBlockExpr>("BLOCK_EXPR") {

        override fun shouldCreateStub(node: ASTNode): Boolean = shouldCreateExprStub(node)

        override fun serialize(stub: AHKBlockExprStub, dataStream: StubOutputStream) {
            dataStream.writeInt(stub.flags)
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): AHKBlockExprStub =
                AHKBlockExprStub(parentStub, this, dataStream.readInt())

        override fun createStub(psi: AHKBlockExpr, parentStub: StubElement<*>?): AHKBlockExprStub {
            val flags = 0
            return AHKBlockExprStub(parentStub, this, flags)
        }

        override fun createPsi(stub: AHKBlockExprStub): AHKBlockExpr = AHKBlockExprImpl(stub, this)
    }
}

/**
 * [IReparseableElementTypeBase] and [ICustomParsingType] are implemented to provide lazy and incremental
 *  parsing of function bodies.
 * [ICompositeElementType] - to create AST of type [LazyParseableElement] in the case of non-lazy parsing
 *  (`if` bodies, `match` arms, etc), just to have the same AST class for all code blocks (I'm not sure
 *  if that makes sense; made just in case)
 * [ILightLazyParseableElementType] is needed to diff trees correctly (see `PsiBuilderImpl.MyComparator`).
 */
object AHKBlockStubType : AHKPlaceholderStub.Type<AHKBlock>("BLOCK", ::AHKBlockImpl),
        ICustomParsingType,
        ICompositeElementType,
        IReparseableElementTypeBase,
        ILightLazyParseableElementType {

    /** Note: must return `false` if [StubBuilder.skipChildProcessingWhenBuildingStubs] returns `true` for the [node] */
    override fun shouldCreateStub(node: ASTNode): Boolean {
        return if (node.treeParent.elementType == FUNCTION) {
            BlockVisitor.blockContainsItems(node)
        } else {
            createStubIfParentIsStub(node) || node.findChildByType(AHK_ITEMS) != null
        }
    }

    // Lazy parsed (function body)
    override fun parse(text: CharSequence, table: CharTable): ASTNode = LazyParseableElement(this, text)

    // Non-lazy case (`if` body, etc).
    override fun createCompositeNode(): ASTNode = LazyParseableElement(this, null)

    override fun parseContents(chameleon: ASTNode): ASTNode? {
        val project = chameleon.treeParent.psi.project
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, AHKLanguage, chameleon.chars)
        parseBlock(builder)
        return builder.treeBuilt.firstChildNode
    }

    override fun parseContents(chameleon: LighterLazyParseableNode): FlyweightCapableTreeStructure<LighterASTNode> {
        val project = chameleon.containingFile?.project ?: error("`containingFile` must not be null: $chameleon")
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, AHKLanguage, chameleon.text)
        parseBlock(builder)
        return builder.lightTree
    }

    private fun parseBlock(builder: PsiBuilder) {
        // Should be `AHKParser().parseLight(BLOCK, builder)`, but we don't have parsing rule for `BLOCK`.
        // Here is a copy of `AHKParser.parseLight` method with `AHKParser.InnerAttrsAndBlock` parser.
        // Note: we can't use `AHKParser().parseLight(INNER_ATTRS_AND_BLOCK, builder)` because the root
        // parsed node must be of BLOCK type. Otherwise, tree diff mechanism works incorrectly
        // (see `BlockSupport.ReparsedSuccessfullyException`)
        val adaptBuilder = GeneratedParserUtilBase.adapt_builder_(BLOCK, builder, AHKParser(), AHKParser.EXTENDS_SETS_)
        val marker = GeneratedParserUtilBase.enter_section_(adaptBuilder, 0, GeneratedParserUtilBase._COLLAPSE_, null)
        val result = AHKParser.InnerAttrsAndBlock(adaptBuilder, 0)
        GeneratedParserUtilBase.exit_section_(adaptBuilder, 0, marker, BLOCK, result, true, GeneratedParserUtilBase.TRUE_CONDITION)
    }

    // Restricted to a function body only because it is well tested case. May be unrestricted to any block in future
    override fun isParsable(parent: ASTNode?, buffer: CharSequence, fileLanguage: Language, project: Project): Boolean =
            parent?.elementType == FUNCTION && PsiBuilderUtil.hasProperBraceBalance(buffer, AHKLexer(), LBRACE, RBRACE)

    // Avoid double lexing
    override fun reuseCollapsedTokens(): Boolean = true

    private class BlockVisitor private constructor(): RecursiveTreeElementWalkingVisitor() {
        private var hasItemsOrAttrs = false

        override fun visitNode(element: TreeElement) {
            if (element.elementType in AHK_ITEMS) {
                hasItemsOrAttrs = true
                stopWalking()
            } else {
                super.visitNode(element)
            }
        }

        companion object {
            fun blockContainsItems(node: ASTNode): Boolean {
                val visitor = BlockVisitor()
                (node as TreeElement).acceptTree(visitor)
                return visitor.hasItemsOrAttrs
            }
        }
    }
}

class AHKFileStub(file: AHKFile) : PsiFileStubImpl<AHKFile>(file) {

    override fun getType() = Type

    object Type : IStubFileElementType<AHKFileStub>(AHKLanguage) {
        private const val STUB_VERSION = 1

        // Bump this number if Stub structure changes
        override fun getStubVersion(): Int = 1 + STUB_VERSION

        override fun getBuilder(): StubBuilder = object : DefaultStubBuilder() {
            override fun createStubForFile(file: PsiFile): StubElement<*> {
                TreeUtil.ensureParsed(file.node) // profiler hint
                return AHKFileStub(file as AHKFile)
            }
        }

        override fun getExternalId(): String = "AHK.file"

//        Uncomment to find out what causes switch to the AST
//
//        private val PARESED = com.intellij.util.containers.ContainerUtil.newConcurrentSet<String>()
//        override fun doParseContents(chameleon: ASTNode, psi: com.intellij.psi.PsiElement): ASTNode? {
//            val path = psi.containingFile?.virtualFile?.path
//            if (path != null && PARESED.add(path)) {
//                println("Parsing (${PARESED.size}) $path")
//                val trace = java.io.StringWriter().also { writer ->
//                    Exception().printStackTrace(java.io.PrintWriter(writer))
//                    writer.toString()
//                }
//                println(trace)
//                println()
//            }
//            return super.doParseContents(chameleon, psi)
//        }
    }
}
