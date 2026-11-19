package com.autohotkey.lang.v2;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static com.autohotkey.lang.v2.psi.Ahk2Types.*;
import static com.intellij.psi.TokenType.*;

%%

%public
%class Ahk2Lexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

//generic
WS_HOZ=[ \t]+
LINE_COMMENT=;[^\r\n]*
HOTSTRING=:[ \t]*[A-Za-z0-9*?]*[ \t]*:[^:\r\n]+::

%state POSSIBLE_BOL_COMMENT, CODE, POSSIBLE_EOL_COMMENT

%%

<YYINITIAL> {
    {HOTSTRING} { return HOTSTRING; }
    {LINE_COMMENT} { return LINE_COMMENT; }
    {WS_HOZ} { yybegin(POSSIBLE_BOL_COMMENT); return TEXT; }
    [^:;\r\n \t]+ { yybegin(CODE); return TEXT; }
    :+ { yybegin(CODE); return TEXT; }
    [\r\n]+ { return TEXT; }
}

<POSSIBLE_BOL_COMMENT> {
    {HOTSTRING} { yybegin(CODE); return HOTSTRING; }
    {LINE_COMMENT} { yybegin(CODE); return LINE_COMMENT; }
    {WS_HOZ} { return TEXT; }
    [^:;\r\n \t]+ { yybegin(CODE); return TEXT; }
    :+ { yybegin(CODE); return TEXT; }
    [\r\n]+ { yybegin(YYINITIAL); return TEXT; }
}

<CODE> {
    {LINE_COMMENT} { return TEXT; }
    {WS_HOZ} { yybegin(POSSIBLE_EOL_COMMENT); return TEXT; }
    [^;\r\n \t]+ { return TEXT; }
    [\r\n]+ { yybegin(YYINITIAL); return TEXT; }
}

<POSSIBLE_EOL_COMMENT> {
    {LINE_COMMENT} { yybegin(CODE); return LINE_COMMENT; }
    {WS_HOZ} { return TEXT; }
    [^;\r\n \t]+ { yybegin(CODE); return TEXT; }
    [\r\n]+ { yybegin(YYINITIAL); return TEXT; }
}
