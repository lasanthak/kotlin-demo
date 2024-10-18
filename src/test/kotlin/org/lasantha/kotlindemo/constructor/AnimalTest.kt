package org.lasantha.kotlindemo.constructor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnimalTest {

    @Test
    fun `Test Cat`() {
        Cat("Nemo").also {
            assertEquals("Nemo", it.type)
            assertEquals(false, it.tabby)
            assertEquals(false, it.american)
        }

        Cat("Black Shorthair").also {
            assertEquals("Black Shorthair", it.type)
            assertEquals(true, it.tabby)
            assertEquals(false, it.american)
        }

        Cat("American shorthair").also {
            assertEquals("American shorthair", it.type)
            assertEquals(true, it.tabby)
            assertEquals(true, it.american)
        }
    }
}
