;Following are valid hotstrings
::btw::by the way
:: ::by the way
: * : b ::by the way
:: :::
::btw2::
MsgBox You typed "btw".
return
:*:]d::
:*:j@::jsmith@somedomain.com
:*b0:<em>::</em>{left 5}
:b0*?:11::
::text1::
(
Any text between the top and bottom parentheses is treated literally, including commas and percent signs.
)
:r0:text1::
#IfWinActive
::btw::This

:C:BTW::  ; Typed in all-caps.
:C:Btw::  ; Typed with only the first letter upper-case.
: :btw::  ; Typed in any other combination.
    case_conform_btw() {
        hs := A_ThisHotkey  ; For convenience and in case we're interrupted.
        if (hs == ":C:BTW")
            Send BY THE WAY
        else if (hs == ":C:Btw")
            Send By the way
        else
            Send by the way
    }

;Invalid hotstrings
:::::
