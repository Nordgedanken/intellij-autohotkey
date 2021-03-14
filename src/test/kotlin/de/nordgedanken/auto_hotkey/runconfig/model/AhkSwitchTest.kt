package de.nordgedanken.auto_hotkey.runconfig.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class AhkSwitchTest : FunSpec({
    test("isValidSwitch correctly validates switches") {
        AhkSwitch.isValidSwitch("/invalidSwitch").shouldBeFalse()
        AhkSwitch.isValidSwitch("/ErrorStdOut").shouldBeTrue()
    }

    test("valueOfBySwitchName correctly returns the expected switch or fails") {
        AhkSwitch.valueOfBySwitchName("/ErrorStdOut") shouldBe AhkSwitch.ERROR_STD_OUT
        shouldThrow<NoSuchElementException> {
            AhkSwitch.valueOfBySwitchName("/invalidSwitch")
        }
    }
})
