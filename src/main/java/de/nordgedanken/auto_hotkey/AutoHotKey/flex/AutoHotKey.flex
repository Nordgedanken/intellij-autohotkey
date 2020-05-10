package de.nordgedanken.auto_hotkey;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import de.nordgedanken.auto_hotkey.psi.AHKTypes;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public _AHKLexer() {
    this((java.io.Reader)null);
  }
%}

%{}

  /**
    * Dedicated storage for starting position of some previously successful
    * match
    */
  private int zzPostponedMarkedPos = -1;

  /**
    * Dedicated nested-comment level counter
    */
  private int zzNestedCommentLevel = 0;
%}

%{
  IElementType imbueBlockComment() {
      assert(zzNestedCommentLevel == 0);
      yybegin(YYINITIAL);

      zzStartRead = zzPostponedMarkedPos;
      zzPostponedMarkedPos = -1;

      return AHKTypes.BLOCK_COMMENT;
  }
%}

%public
%class _AHKLexer
%implements FlexLexer
%function advance
%type IElementType

%s WAITING_VALUE
%s IN_BLOCK_COMMENT

%unicode

CRLF=\R

///////////////////////////////////////////////////////////////////////////////////////////////////
// Whitespaces
///////////////////////////////////////////////////////////////////////////////////////////////////

EOL_WS           = \n | \r | \r\n
LINE_WS          = [\ \t]
WHITE_SPACE_CHAR = {EOL_WS} | {LINE_WS}
WHITE_SPACE      = {WHITE_SPACE_CHAR}+


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

C_COMMENT = "#"{KEY}

HOTKEY = ("#"|"!"|"^"|"+"|"&"|"<"|">"|"<^>!"|"*"|"~"|"$")? [a-zA-Z]? "::"

STRING_CALL = "%"{KEY}"%"

HEX = "0x"([A-Fa-f0-9])*

%%



<YYINITIAL> {
    {WHITE_SPACE}                                           { return WHITE_SPACE;                                     }

    "+"                                                     { return AHKTypes.PLUS;                                   }
    "-"                                                     { return AHKTypes.MINUS;                                  }
    "*"                                                     { return AHKTypes.MUL;                                    }
    "/"                                                     { return AHKTypes.QUOTIENT;                               }
    {LPAREN}                                                { return AHKTypes.LPAREN;                                 }
    {RPAREN}                                                { return AHKTypes.RPAREN;                                 }
    {LBRACE}                                                { return AHKTypes.LBRACE;                                 }
    {RBRACE}                                                { return AHKTypes.RBRACE;                                 }
    ";"                                                     { return AHKTypes.SEMICOLON;                              }
    ":"                                                     { return AHKTypes.COLON;                                  }
    ","                                                     { return AHKTypes.COMMA;                                  }
    "="                                                     { return AHKTypes.EQUAL;                                  }
    "!="                                                    { return AHKTypes.NOT_EQ;                                 }
    "<"                                                     { return AHKTypes.LESS;                                   }
    ">"                                                     { return AHKTypes.GREATER;                                }
    "<="                                                    { return AHKTypes.LESS_OR_EQUAL;                          }
    ">="                                                    { return AHKTypes.GREATER_OR_EQUAL;                       }
    "if"|"?"                                                { return AHKTypes.IF;                                     }
    "%"                                                     { return AHKTypes.EXPRESSION_SCRIPT;                      }
    {END_OF_LINE_COMMENT}                                   { return AHKTypes.COMMENT;                                }

    {KEY}                                                   { return AHKTypes.KEY;                                    }

    {STRING_CALL}                                           { return AHKTypes.STRING_CALL;                            }

    {STRING}                                                { return AHKTypes.STRING;                                 }

    {VAR_ASIGN}                                             { return AHKTypes.VAR_ASIGN;                              }

    {C_COMMENT}                                             { return AHKTypes.C_COMMENT;                              }

    //TODO this might have side effects
    //{LBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;     }

     //TODO this might have side effects
    //{RBRACE}                                                { yybegin(YYINITIAL); return AHKTypes.FUNCTION_DEF;     }

    {NUMBER}+                                               { return AHKTypes.NUMBER;                                 }

    "%"                                                     { return AHKTypes.EXPRESSION_SCRIPT;                      }

    {HOTKEY}                                                { return AHKTypes.HOTKEY;                                 }

    {HEX}                                                   { return AHKTypes.HEX;                                    }

    {KEY}                                                   { return AHKTypes.KEY;                                    }

    "/*"                                                    { yybegin(IN_BLOCK_COMMENT); yypushback(2);               }
}

<WAITING_VALUE> {
    {STRING}                                                { yybegin(YYINITIAL); return AHKTypes.STRING;             }

    {CRLF}({CRLF}|{WHITE_SPACE})+                           { yybegin(YYINITIAL); return WHITE_SPACE;                 }

    {WHITE_SPACE}+                                          { yybegin(WAITING_VALUE); return WHITE_SPACE;             }

    {NUMBER}+                                               { yybegin(YYINITIAL); return AHKTypes.NUMBER;             }

    {KEY}                                                   { yybegin(YYINITIAL); return AHKTypes.KEY;                }
}


///////////////////////////////////////////////////////////////////////////////////////////////////
// Comments
///////////////////////////////////////////////////////////////////////////////////////////////////

<IN_BLOCK_COMMENT> {
  "/*"    { if (zzNestedCommentLevel++ == 0)
              zzPostponedMarkedPos = zzStartRead;
          }

  "*/"    { if (--zzNestedCommentLevel == 0)
              return imbueBlockComment();
          }

  <<EOF>> { zzNestedCommentLevel = 0; return imbueBlockComment(); }

  [^]     { }
}



// TODO S: &KEY

[^]                                                         { return BAD_CHARACTER;                                   }
