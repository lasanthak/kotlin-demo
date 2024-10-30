package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf

class ProducerMonadTest {

    @Test
    fun `Identity Test`() {
        object : Producer<Long, RuntimeException> {
            override fun produce(): Producible<Long, RuntimeException> {
                return Producible.Result(44L)
            }
        }.produce().also { actual ->
            // Identity test for creation
            assertEquals(actual, Producible.Result(44L))
            // Identity test for Monad
            assertEquals(actual, actual.flatMap { Producible.Result(it) })
            // Functor identity test for Monad
            assertEquals(actual, actual.map { it })
        }

        object : Producer<Long, RuntimeException> {
            override fun produce(): Producible<Long, RuntimeException> {
                return Producible.Failure(IllegalArgumentException("Failed to produce"))
            }
        }.produce().also { actual ->
            assertInstanceOf<Producible.Failure<RuntimeException>>(actual)
            assertInstanceOf<IllegalArgumentException>(actual.explanation)
            assertEquals("Failed to produce", actual.explanation.message)
            // Identity test for Monad
            assertEquals(actual, actual.flatMap { Producible.Result(it) })
            // Functor identity test for Monad
            assertEquals(actual, actual.map { it })
        }
    }
}
