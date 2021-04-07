;normal directives. Leading whitespace and suffix commas allowed
#SingleInstance, Force
  #IfWinActive ahk_exe chrome.exe

;should not be read as directives
#a::Msgbox hi
#SingleInstance: Force

/* two directives can not be on the same line. The 2nd is ignored (the 1st also would not work, but
we're ignoring that detail for now
*/
#NoEnv #Warn

;technically a valid directive name, but it doesn't really exist so it does nothing
#NotADirective1

;directives can follow a block comment
/* test
*/ #Warn