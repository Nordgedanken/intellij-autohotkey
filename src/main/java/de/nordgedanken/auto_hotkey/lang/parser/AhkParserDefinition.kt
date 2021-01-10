package de.nordgedanken.auto_hotkey.lang.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.lang.core.AhkLanguage
import de.nordgedanken.auto_hotkey.lang.lexer.AhkLexerAdapter
import de.nordgedanken.auto_hotkey.lang.psi.AhkFile
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.*
import de.nordgedanken.auto_hotkey.lang.psi.*

class AhkParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = AhkLexerAdapter()

    override fun getWhitespaceTokens() = WHITESPACE_TOKENS

    override fun getCommentTokens() = COMMENT_TOKENS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project?): PsiParser = AhkParser()

    override fun getFileNodeType() = IFileElementType(AhkLanguage)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = AhkFile(viewProvider)

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode) = ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode?): PsiElement =
            Factory.createElement(node)
}
