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

%%

[^]+ { return TEXT; }
