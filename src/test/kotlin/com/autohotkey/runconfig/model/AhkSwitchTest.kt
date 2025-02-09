package com.autohotkey.runconfig.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AhkSwitchTest {
    @Test
    fun `test isValidSwitch`() {
        assertFalse(AhkSwitch.isValidSwitch("/invalidSwitch"))
        assertTrue(AhkSwitch.isValidSwitch("/ErrorStdOut"))
    }

    @Test
    fun `test valueOfBySwitchName`() {
        assertEquals(AhkSwitch.ERROR_STD_OUT, AhkSwitch.valueOfBySwitchName("/ErrorStdOut"))
        assertFailsWith<NoSuchElementException> {
            AhkSwitch.valueOfBySwitchName("/invalidSwitch")
        }
    }
}
