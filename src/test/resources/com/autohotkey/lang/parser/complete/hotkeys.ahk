;Following are valid hotkeys
a::
\::
^::
::: ;ignoring this to simplify parse logic for now
^a::
+a::
!F2::
^+a::
#n::
^!s::MsgBox hi
<^>!m::MsgBox You pressed AltGr+m
<^<!m::MsgBox You pressed LeftControl+LeftAlt+m
LControl & RAlt::MsgBox You pressed AltGr itself
*#c::
*ScrollLock::
~RButton::
~RButton & C::
AppsKey Up::
~AppsKey & <::
~AppsKey & >::
*LWin Up::
LControl & F1::
LControl::
^Numpad0::
Numpad0 & Numpad1::
Alt & /::
~LControl & WheelDown::
MButton::
~*Esc::
RButton & WheelUp::
F1 & e Up::

;Following should be parsed as hotkeys even though they are technically invalid
;An annotator that does more in-depth analysis can verify the validity of a hotkey
fadfaf::
^+^+^::
dfasdf &&*& fdas::
ds ^^ fda Upp::

;Following should not be read as hotkeys at all
#a:
test ::
a:
::btw::by the way
:*:]d::dd
