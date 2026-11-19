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

// Hotstring is :options:abbreviation:: (eg ":*:btw::")
HOTSTRING=:[ \t]*[A-Za-z0-9*?]*[ \t]*:[^:\r\n]+::

// Hotkey must be a run of non-space chars (optionally separated by spaces, eg "LControl & RAlt" or "AppsKey Up") followed by ::
HOTKEY=[^:;\r\n \t]+([ \t]+[^:;\r\n \t]+)*::

// Just the directive name. Its parameters are lexed as normal text and we don't check that the name is a real directive
DIRECTIVE=#[A-Za-z0-9_]+

%state CODE, POSSIBLE_EOL_COMMENT

%%

// Beginning of line: hotkeys, hotstrings, directives and comments are only valid here (after optional whitespace)
<YYINITIAL> {
    {HOTSTRING} { yybegin(CODE); return HOTSTRING; }
    // Hotkeys can start with a # like directives do (eg "#a::"), but they always match more chars
    {HOTKEY} { yybegin(CODE); return HOTKEY; }
    {LINE_COMMENT} { yybegin(CODE); return LINE_COMMENT; }
    // v2 requires whitespace (or the end of the line) after the directive name, so "#Name: param" isn't a directive.
    // Must come before the DIRECTIVE rule
    {DIRECTIVE} / [:;] { yybegin(CODE); return TEXT; }
    // Must come before the generic word rule below. If any other char follows the name (eg "#Name, param"), the
    // word rule matches more chars and wins, so that isn't a directive either
    {DIRECTIVE} { yybegin(CODE); return DIRECTIVE; }
    {WS_HOZ} { return TEXT; }
    [^:;\r\n \t]+ { yybegin(CODE); return TEXT; }
    :+ { yybegin(CODE); return TEXT; }
    [\r\n]+ { return TEXT; }
}

// After the first token of a line. A comment is not possible until whitespace is seen
<CODE> {
    {LINE_COMMENT} { return TEXT; }
    {WS_HOZ} { yybegin(POSSIBLE_EOL_COMMENT); return TEXT; }
    [^;\r\n \t]+ { return TEXT; }
    [\r\n]+ { yybegin(YYINITIAL); return TEXT; }
}

// After whitespace within a line, where a line comment can start
<POSSIBLE_EOL_COMMENT> {
    {LINE_COMMENT} { yybegin(CODE); return LINE_COMMENT; }
    {WS_HOZ} { return TEXT; }
    [^;\r\n \t]+ { yybegin(CODE); return TEXT; }
    [\r\n]+ { yybegin(YYINITIAL); return TEXT; }
}
