package de.nordgedanken.auto_hotkey.lang.parser

import com.intellij.lang.ASTNode
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
import de.nordgedanken.auto_hotkey.lang.core.AhkLanguage
import de.nordgedanken.auto_hotkey.lang.lexer.AhkLexerAdapter
import de.nordgedanken.auto_hotkey.lang.psi.AhkFile
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes

class AhkParserDefinition : ParserDefinition {
    val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    val COMMENTS = TokenSet.create(TokenType.DUMMY_HOLDER)
    val FILE = IFileElementType(AhkLanguage)

    override fun createLexer(project: Project?): Lexer = AhkLexerAdapter()

    override fun getWhitespaceTokens() = WHITE_SPACES

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements() = TokenSet.EMPTY

    override fun createParser(project: Project?): PsiParser = AhkParser()

    override fun getFileNodeType() = FILE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = AhkFile(viewProvider)

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode) = ParserDefinition.SpaceRequirements.MAY

/*    override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        if (left.elementType in AHK_EOL_COMMENTS) return ParserDefinition.SpaceRequirements.MUST_LINE_BREAK
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, AhkLexerAdapter())
    }*/

    override fun createElement(node: ASTNode?): PsiElement =
            AhkTypes.Factory.createElement(node)

    /*companion object {
        @JvmField
        val BLOCK_COMMENT = AhkTokenType("<BLOCK_COMMENT>")
        @JvmField
        val EOL_COMMENT = AhkTokenType("<EOL_COMMENT>")
    }*/
}
