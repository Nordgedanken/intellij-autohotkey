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

KEY_CHARACTER=[a-zA-Z] | "\_" | "."
KEY={KEY_CHARACTER}+
NUMBER="-"? [0-9]

STR =      "\""
STRING = {STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES}? | [0-8xuU] ) )* {STR}?
ESCAPES = [abfnrtv]

LPAREN = "("
RPAREN = ")"
LBRACE = "{"
RBRACE = "}"
FUNCTION_CALL_START = {KEY_CHARACTER}+ {LPAREN}
FUNCTION_CALL_END = {RPAREN}

C_COMMENT = "#"{KEY}

HOTKEY = ("#"|"!"|"^"|"+"|"&"|"<"|">"|"<^>!"|"*"|"~"|"$")? [a-zA-Z]? "::"

STRING_CALL = "%"{KEY}"%"

HEX = "0x"([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})

%state WAITING_VALUE
%state START_FUNCTION

%%



<YYINITIAL> {
    {END_OF_LINE_COMMENT}                                   { yybegin(YYINITIAL); return AHKTypes.COMMENT;            }

    {FUNCTION_CALL_START}                                   { yybegin(START_FUNCTION); return AHKTypes.FUNCTION_CALL; }

    {KEY}                                                   { yybegin(YYINITIAL); return AHKTypes.KEY;                }

    {STRING_CALL}                                           { yybegin(YYINITIAL); return AHKTypes.STRING_CALL;        }

    {STRING}                                                { yybegin(YYINITIAL); return AHKTypes.STRING;             }

    {VAR_ASIGN}                                             { yybegin(WAITING_VALUE); return AHKTypes.VAR_ASIGN;      }

    {C_COMMENT}                                             { yybegin(YYINITIAL); return AHKTypes.C_COMMENT;          }

    //TODO this might have side effects
    {LBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;       }

     //TODO this might have side effects
    {RBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;       }

    {NUMBER}+                                               { yybegin(YYINITIAL); return AHKTypes.NUMBER;             }

    "%"                                                     { yybegin(YYINITIAL); return AHKTypes.EXPRESSION_SCRIPT;  }

    {HOTKEY}                                                { yybegin(YYINITIAL); return AHKTypes.HOTKEY;             }

    {HEX}                                                   { yybegin(YYINITIAL); return AHKTypes.HEX;                }
}

<WAITING_VALUE> {
    {FUNCTION_CALL_START}                                   { yybegin(START_FUNCTION); return AHKTypes.FUNCTION_CALL; }

    {STRING}                                                { yybegin(YYINITIAL); return AHKTypes.STRING;             }

    {CRLF}({CRLF}|{WHITE_SPACE})+                           { yybegin(YYINITIAL); return WHITE_SPACE;                 }

    {WHITE_SPACE}+                                          { yybegin(WAITING_VALUE); return WHITE_SPACE;             }

    {NUMBER}+                                               { yybegin(YYINITIAL); return AHKTypes.NUMBER;             }

    {KEY}                                                   { yybegin(YYINITIAL); return AHKTypes.KEY;                }
}

<START_FUNCTION> {
    {STRING}                                                { yybegin(START_FUNCTION); return AHKTypes.STRING;        }

    {KEY}                                                   { yybegin(START_FUNCTION); return AHKTypes.KEY;           }

    {NUMBER}+                                               { yybegin(START_FUNCTION); return AHKTypes.NUMBER;        }

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
":"                                                         { return AHKTypes.COLON;                                  }
","                                                         { return AHKTypes.COMMA;                                  }
"="                                                         { return AHKTypes.EQUAL;                                  }
"!="                                                        { return AHKTypes.NOT_EQ;                                 }
"<"                                                         { return AHKTypes.LESS;                                   }
">"                                                         { return AHKTypes.GREATER;                                }
"<="                                                        { return AHKTypes.LESS_OR_EQUAL;                          }
">="                                                        { return AHKTypes.GREATER_OR_EQUAL;                       }
"if"|"?"                                                    { return AHKTypes.IF;                                     }
"%"                                                         { return AHKTypes.EXPRESSION_SCRIPT;                      }

// TODO S: &KEY

[^]                                                         { return BAD_CHARACTER;                                   }
