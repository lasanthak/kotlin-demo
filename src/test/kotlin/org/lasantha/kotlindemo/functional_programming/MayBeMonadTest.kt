package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MayBeMonadTest {

    private fun Int.maybeEven(): MayBe<Int> = if (this % 2 == 0) MayBe.Just(this) else MayBe.None

    @Test
    fun `Composition test`() {
        val maybe: (Int) -> MayBe<Int> = { i ->
            MayBe.Just(i)
                .flatMap { if (it >= 0) MayBe.Just(it) else MayBe.None }
                .flatMap { if (it <= 100) MayBe.Just(it) else MayBe.None }

        }

        assertEquals(MayBe.Just(50), maybe(50))
        assertEquals(MayBe.Just(100), maybe(100))
        assertEquals(MayBe.Just(0), maybe(0))
        assertEquals(MayBe.None, maybe(-1))
        assertEquals(MayBe.None, maybe(101))
    }

    @Test
    fun `Extension function composition test`() {
        assertEquals(MayBe.Just(50), 50.maybeEven())
        assertEquals(MayBe.Just(100), 100.maybeEven())
        assertEquals(MayBe.Just(0), 0.maybeEven())
        assertEquals(MayBe.None, (-1).maybeEven())
        assertEquals(MayBe.None, 101.maybeEven())
    }
}
