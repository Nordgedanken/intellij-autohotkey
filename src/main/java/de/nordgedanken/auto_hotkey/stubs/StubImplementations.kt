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
import de.nordgedanken.auto_hotkey.AHKLanguage
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.parser.AHKParser
import de.nordgedanken.auto_hotkey.psi.AHKBlock
import de.nordgedanken.auto_hotkey.psi.AHKBlockExpr
import de.nordgedanken.auto_hotkey.psi.AHKFile
import de.nordgedanken.auto_hotkey.psi.AHKTypes.*
import de.nordgedanken.auto_hotkey.psi.AHK_ITEMS
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement
import de.nordgedanken.auto_hotkey.psi.impl.AHKBlockExprImpl
import de.nordgedanken.auto_hotkey.psi.impl.AHKBlockImpl
import de.nordgedanken.auto_hotkey.psi.impl.AHKRetExprImpl

fun factory(name: String): AHKStubElementType<*, *> = when (name) {
    "FUNCTION" -> AHKFunctionStub.Type
    "BLOCK" -> AHKBlockStubType
    "BLOCK_EXPR" -> AHKBlockExprStub.Type
    "RET_EXPR" -> AHKExprStubType("RET_EXPR", ::AHKRetExprImpl)
    else -> error("Unknown element $name")
}

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
