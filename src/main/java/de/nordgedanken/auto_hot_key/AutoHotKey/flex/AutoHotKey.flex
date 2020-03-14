package de.nordgedanken.auto_hot_key;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import de.nordgedanken.auto_hot_key.psi.AHKTypes;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%class AHKLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
END_OF_LINE_COMMENT=(";")[^\r\n]*
VAR_ASIGN=":="

KEY_CHARACTER=[^:=\ \n\t\f\\\(\)\,\{\}] | "\\ "
KEY={KEY_CHARACTER}+

STR =      "\""
STRING = {STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES}? | [0-8xuU] ) )* {STR}?
ESCAPES = [abfnrtv]

LPAREN = "("
RPAREN = ")"
LBRACE = "{"
RBRACE = "}"
FUNCTION_CALL_START = {KEY_CHARACTER}+ {LPAREN}
FUNCTION_CALL_END = {RPAREN}

%state WAITING_VALUE
%state START_FUNCTION

%%



<YYINITIAL> {
    {END_OF_LINE_COMMENT}                                   { yybegin(YYINITIAL); return AHKTypes.COMMENT;            }

    {FUNCTION_CALL_START}                                   { yybegin(START_FUNCTION); return AHKTypes.FUNCTION_CALL; }

    {KEY}                                                   { yybegin(YYINITIAL); return AHKTypes.KEY;                }

    {STRING}                                                { yybegin(YYINITIAL); return AHKTypes.STRING;             }

    {VAR_ASIGN}                                             { yybegin(WAITING_VALUE); return AHKTypes.VAR_ASIGN;      }

    //TODO this might have side effects
    {LBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;       }

     //TODO this might have side effects
    {RBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;       }

    ","                                                     { yybegin(YYINITIAL); return AHKTypes.COMMA;              }
}

<WAITING_VALUE> {
    {FUNCTION_CALL_START}                                   { yybegin(START_FUNCTION); return AHKTypes.FUNCTION_CALL; }

    {STRING}                                                { yybegin(YYINITIAL); return AHKTypes.STRING;             }

    {CRLF}({CRLF}|{WHITE_SPACE})+                           { yybegin(YYINITIAL); return WHITE_SPACE;                 }

    {WHITE_SPACE}+                                          { yybegin(WAITING_VALUE); return WHITE_SPACE;             }

    {KEY}                                                   { yybegin(YYINITIAL); return AHKTypes.KEY;                }
}

<START_FUNCTION> {
    {STRING}                                                { yybegin(START_FUNCTION); return AHKTypes.STRING;        }
    {KEY}                                                   { yybegin(START_FUNCTION); return AHKTypes.KEY;           }
    {FUNCTION_CALL_END}                                     { yybegin(YYINITIAL); return AHKTypes.FUNCTION_CALL;      }
}

({CRLF}|{WHITE_SPACE})+                                     { yybegin(YYINITIAL); return WHITE_SPACE;                 }

"+"                                                         { return AHKTypes.PLUS;                                   }
"-"                                                         { return AHKTypes.MINUS;                                  }
"*"                                                         { return AHKTypes.MUL;                                    }
"/"                                                         { return AHKTypes.QUOTIENT;                               }
{LPAREN}                                                    { return AHKTypes.LPAREN;                                 }
{RPAREN}                                                    { return AHKTypes.RPAREN;                                 }
{LBRACE}                                                    { return AHKTypes.LBRACE;                                 }
{RBRACE}                                                    { return AHKTypes.RBRACE;                                 }
";"                                                         { return AHKTypes.SEMICOLON;                              }
","                                                         { return AHKTypes.COMMA;                                  }
"."                                                         { return AHKTypes.DOT;                                    }
"="                                                         { return AHKTypes.EQUAL;                                  }
"<"                                                         { return AHKTypes.LESS;                                   }
">"                                                         { return AHKTypes.GREATER;                                }
"<="                                                        { return AHKTypes.LESS_OR_EQUAL;                          }
">="                                                        { return AHKTypes.GREATER_OR_EQUAL;                       }
"if"                                                        { return AHKTypes.IF;                                     }

[^]                                                         { return BAD_CHARACTER;                                   }
