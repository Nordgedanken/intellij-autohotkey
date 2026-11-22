;Following are valid directives. Leading whitespace is allowed
#SingleInstance Force
  #HotIf WinActive("ahk_exe chrome.exe")
#Include "lib.ahk"
#Requires AutoHotkey v2.0
#Warn

;Directive names are not case sensitive and can be followed by a comment
#singleinstance force
#Warn ;comment
#Warn All  ; comment

;Following should be parsed as directives even though they are technically invalid
;An annotator that does more in-depth analysis can verify the validity of a directive
#NotADirective1
#Not_A_Directive

;Only the directive name is parsed as a directive, so a 2nd directive on the same line is just text
;(the 1st one would not work either in AutoHotkey, but we're ignoring that detail for now)
#SingleInstance Force #Warn

;Following should not be read as directives at all
#a::MsgBox hi
#a Up::MsgBox hi
#SingleInstance: Force
#SingleInstance, Force
#Warn;comment
# Warn
#
MsgBox #Warn

;A directive can be the last line of the file
#Warn