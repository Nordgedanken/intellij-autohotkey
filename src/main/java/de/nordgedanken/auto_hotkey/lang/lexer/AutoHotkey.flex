package de.nordgedanken.auto_hotkey;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import de.nordgedanken.auto_hotkey.lang.psi.AhkTypes;

%%

%public
%class AhkLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
FIRST_VALUE_CHARACTER=[^ \n\f\\] | "\\"{CRLF} | "\\".
VALUE_CHARACTER=[^\n\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT=("#"|"!")[^\r\n]*
SEPARATOR=[:=]
KEY_CHARACTER=[^:=\ \n\t\f\\] | "\\ "

%state WAITING_VALUE

%%

<YYINITIAL> {END_OF_LINE_COMMENT}                           { yybegin(YYINITIAL); return AhkTypes.COMMENT; }

<YYINITIAL> {KEY_CHARACTER}+                                { yybegin(YYINITIAL); return AhkTypes.KEY; }

<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return AhkTypes.SEPARATOR; }

<WAITING_VALUE> {CRLF}({CRLF}|{WHITE_SPACE})+               { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*   { yybegin(YYINITIAL); return AhkTypes.VALUE; }

({CRLF}|{WHITE_SPACE})+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }

//%{
//  public _AHKLexer() {
//    this((java.io.Reader)null);
//  }
//%}
//
//%{}
//
//  /**
//    * Dedicated storage for starting position of some previously successful
//    * match
//    */
//  private int zzPostponedMarkedPos = -1;
//
//  /**
//    * Dedicated nested-comment level counter
//    */
//  private int zzNestedCommentLevel = 0;
//%}
//
//%{
//  IElementType imbueBlockComment() {
//      assert(zzNestedCommentLevel == 0);
//      yybegin(YYINITIAL);
//
//      zzStartRead = zzPostponedMarkedPos;
//      zzPostponedMarkedPos = -1;
//
//      return BLOCK_COMMENT;
//  }
//%}
//
//%public
//%class _AHKLexer
//%implements FlexLexer
//%function advance
//%type IElementType
//
//%s IN_BLOCK_COMMENT
//
//%unicode
//
//
/////////////////////////////////////////////////////////////////////////////////////////////////////
//// Identifier
/////////////////////////////////////////////////////////////////////////////////////////////////////
//IDENTIFIER = [_\p{xidstart}][\p{xidcontinue}]*
//SUFFIX     = {IDENTIFIER}
/////////////////////////////////////////////////////////////////////////////////////////////////////
//// Whitespaces
/////////////////////////////////////////////////////////////////////////////////////////////////////
//
//EOL_WS           = \n | \r | \r\n
//LINE_WS          = [\ \t]
//WHITE_SPACE_CHAR = {EOL_WS} | {LINE_WS}
//WHITE_SPACE      = {WHITE_SPACE_CHAR}+
//
//
//END_OF_LINE_COMMENT=(";")[^\r\n]*
//
//VAR_ASIGN=":="
//
//NUMBERS="-"? [0-9]*
//
//STR =      "\""
//STRING = {STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES}? | [0-8xuU] ) )* {STR}?
//ESCAPES = [abfnrtv]
//
//C_COMMENT = "#"{IDENTIFIER}
//
//HOTKEY = ("#"|"!"|"^"|"+"|"&"|"<"|">"|"<^>!"|"*"|"~"|"$")? [a-zA-Z]? "::"
//
//INT_LITERAL = ( {DEC_LITERAL}
//              | {HEX_LITERAL}
//              | {OCT_LITERAL}
//              | {BIN_LITERAL} ) {SUFFIX}?
//
//DEC_LITERAL = [0-9] [0-9_]*
//HEX_LITERAL = "0x" [a-fA-F0-9_]*
//OCT_LITERAL = "0o" [0-7_]*
//BIN_LITERAL = "0b" [01_]*
//
//STRING_LITERAL = \" ( [^\\\"] | \\[^] | [\\])* ( \" {SUFFIX}? | \\ )?
//%%
//
//
//
//<YYINITIAL> {
//
//    "+"                                                     { return AHKTypes.PLUS;                                   }
//    "-"                                                     { return AHKTypes.MINUS;                                  }
//    "*"                                                     { return AHKTypes.MUL;                                    }
//    "/"                                                     { return AHKTypes.QUOTIENT;                               }
//    "("                                                     { return AHKTypes.LPAREN;                                 }
//    ")"                                                     { return AHKTypes.RPAREN;                                 }
//    "{"                                                     { return AHKTypes.LBRACE;                                 }
//    "}"                                                     { return AHKTypes.RBRACE;                                 }
//    ";"                                                     { return AHKTypes.SEMICOLON;                              }
//    ":"                                                     { return AHKTypes.COLON;                                  }
//    ","                                                     { return AHKTypes.COMMA;                                  }
//    "."                                                     { return AHKTypes.DOT;                                    }
//    "="                                                     { return AHKTypes.EQUAL;                                  }
//    "!="                                                    { return AHKTypes.NOT_EQ;                                 }
//    "<"                                                     { return AHKTypes.LESS;                                   }
//    ">"                                                     { return AHKTypes.GREATER;                                }
//    "<="                                                    { return AHKTypes.LESS_OR_EQUAL;                          }
//    ">="                                                    { return AHKTypes.GREATER_OR_EQUAL;                       }
//    "if"|"?"                                                { return AHKTypes.IF;                                     }
//    "%"                                                     { return AHKTypes.EXPRESSION_SCRIPT;                      }
//    "Return"                                                { return AHKTypes.RETURN;                                 }
//    {END_OF_LINE_COMMENT}                                   { return EOL_COMMENT;                                     }
//
//
//    {STRING_LITERAL}                                        { return AHKTypes.STRING_LITERAL;                         }
//    {INT_LITERAL}                                           { return AHKTypes.INTEGER_LITERAL;                        }
//
//    {VAR_ASIGN}                                             { return AHKTypes.VAR_ASIGN;                              }
//
//    {C_COMMENT}                                             { return AHKTypes.C_COMMENT;                              }
//
//    //TODO this might have side effects
//    //{LBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;     }
//
//     //TODO this might have side effects
//    //{RBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;     }
//
//    {NUMBERS}                                               { return AHKTypes.NUMBERS;                                }
//
//    {HOTKEY}                                                { return AHKTypes.HOTKEY;                                 }
//
//    {HEX_LITERAL}                                           { return AHKTypes.HEX;                                    }
//
//    "/*"                                                    { yybegin(IN_BLOCK_COMMENT); yypushback(2);               }
//    {IDENTIFIER}                                            { return AHKTypes.IDENTIFIER;                             }
//    {WHITE_SPACE}                                           { return WHITE_SPACE;                                     }
//}
//
//
/////////////////////////////////////////////////////////////////////////////////////////////////////
//// Comments
/////////////////////////////////////////////////////////////////////////////////////////////////////
//
//<IN_BLOCK_COMMENT> {
//  "/*"    { if (zzNestedCommentLevel++ == 0)
//              zzPostponedMarkedPos = zzStartRead;
//          }
//
//  "*/"    { if (--zzNestedCommentLevel == 0)
//              return imbueBlockComment();
//          }
//
//  <<EOF>> { zzNestedCommentLevel = 0; return imbueBlockComment(); }
//
//  [^]     { }
//}
//
//
//
//// TODO S: &KEY
//
//[^]                                                         { return BAD_CHARACTER;                                   }
