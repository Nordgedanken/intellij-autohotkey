package com.autohotkey.lang.parser

import com.autohotkey.lang.core.Ahk2Language
import com.autohotkey.lang.psi.Ahk2File
import com.autohotkey.lang.v2.lexer.Ahk2LexerAdapter
import com.autohotkey.lang.v2.parser.Ahk2Parser
import com.autohotkey.lang.v2.psi.Ahk2Types.Factory
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

class Ahk2ParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = Ahk2LexerAdapter()

    override fun getWhitespaceTokens(): TokenSet = TokenSet.EMPTY

    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project?): PsiParser = Ahk2Parser()

    override fun getFileNodeType() = IFileElementType(Ahk2Language)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = Ahk2File(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(
        left: ASTNode,
        right: ASTNode,
    ) = ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode?): PsiElement = Factory.createElement(node)
}
