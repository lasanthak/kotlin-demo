package org.lasantha.kotlindemo.operators

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrePostOperatorsTest {

    @Test
    fun `Test preInc`() {
        PrePostOperators(100).also {
            assertEquals(100, it.getValue())
            assertEquals(101, it.preInc())
            assertEquals(101, it.getValue())
            assertEquals(101, it.postInc())
            assertEquals(102, it.getValue())
        }
    }
}
