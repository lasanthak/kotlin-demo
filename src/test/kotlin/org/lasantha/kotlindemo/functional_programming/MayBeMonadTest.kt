package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MayBeMonadTest {

    private fun Int.maybeEven(): MayBe<Int> = if (this % 2 == 0) MayBe.Just(this) else MayBe.None

    @Test
    fun `Composition test`() {
        val percentage: (Int) -> MayBe<Int> = { i ->
            MayBe.Just(i)
                .flatMap { if (it >= 0) MayBe.Just(it) else MayBe.None }
                .flatMap { if (it <= 100) MayBe.Just(it) else MayBe.None }
        }

        assertEquals(MayBe.Just(50), percentage(50))
        assertEquals(MayBe.Just(100), percentage(100))
        assertEquals(MayBe.Just(0), percentage(0))
        assertEquals(MayBe.None, percentage(-1))
        assertEquals(MayBe.None, percentage(101))

        val p75: (Int) -> MayBe<Int> = { i ->
            MayBe.Just(i)
                .flatMap(percentage)
                .flatMap { if (it >= 75) MayBe.Just(it) else MayBe.None }
        }
        assertEquals(MayBe.None, p75(50))
        assertEquals(MayBe.Just(75), p75(75))
    }

    @Test
    fun `Extension function composition test`() {
        assertEquals(MayBe.Just(50), 50.maybeEven())
        assertEquals(MayBe.Just(100), 100.maybeEven())
        assertEquals(MayBe.Just(0), 0.maybeEven())
        assertEquals(MayBe.None, (-1).maybeEven())
        assertEquals(MayBe.None, 101.maybeEven())

        when (val result = 50.maybeEven()) {
            is MayBe.Just -> result.value
            is MayBe.None -> throw IllegalStateException("Unexpected None")
        }
    }

    @Test
    fun `Identity test`() {
        assertEquals(MayBe.Just("foo"), MayBe.Just(MayBe.Just("foo")).flatMap { it })
        assertEquals(MayBe.Just(MayBe.Just("foo")), MayBe.Just(MayBe.Just("foo")).map { it })
        assertEquals(MayBe.None, MayBe.None.flatMap<Any> { it })
        assertEquals(MayBe.None, MayBe.None.map<Any> { it })
    }

    @Test
    fun `Test getOrNull`() {
        val f: (String) -> MayBe<String> = { s ->
            MayBe.Just(s).flatMap { if (it.contains(' ')) MayBe.None else MayBe.Just(it) }
        }

        assertTrue(f("Hello").isJust)
        assertEquals("Hello", f("Hello").getOrNull())
        assertFalse(f("Hello World").isJust)
        assertNull(f("Hello World").getOrNull())
    }

    @Test
    fun `Unit test`() {
        assertEquals(MayBe.Just(1), MayBe.Just(1))
        assertEquals(MayBe.Just(MayBe.Just("foo")), MayBe.Just(MayBe.Just("foo")))
        assertEquals(MayBe.None, MayBe.None)
        assertNotEquals(MayBe.None, MayBe.Just(1))
    }
}
