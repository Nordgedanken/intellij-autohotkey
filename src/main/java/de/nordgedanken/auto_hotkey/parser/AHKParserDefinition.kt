package de.nordgedanken.auto_hotkey.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageUtil
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.psi.*
import de.nordgedanken.auto_hotkey.psi.AHKTypes.STRING_LITERAL
import de.nordgedanken.auto_hotkey.stubs.AHKFileStub

class AHKParserDefinition : ParserDefinition {

    override fun createFile(viewProvider: FileViewProvider): PsiFile = AHKFile(viewProvider)

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        if (left.elementType in AHK_EOL_COMMENTS) return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, AHKLexer())
    }

    override fun getFileNodeType(): IFileElementType = AHKFileStub.Type

    override fun getStringLiteralElements(): TokenSet =
            TokenSet.create(STRING_LITERAL)

    override fun getWhitespaceTokens(): TokenSet =
            TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens() = AHK_COMMENTS

    override fun createElement(node: ASTNode?): PsiElement =
            AHKTypes.Factory.createElement(node)

    override fun createLexer(project: Project?): Lexer = AHKLexer()

    override fun createParser(project: Project?): PsiParser = AHKParser()

    companion object {
        @JvmField
        val BLOCK_COMMENT = AHKTokenType("<BLOCK_COMMENT>")
        @JvmField
        val EOL_COMMENT = AHKTokenType("<EOL_COMMENT>")
    }
}
