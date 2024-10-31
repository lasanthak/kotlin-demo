package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import kotlin.random.Random

class ProducerFunctorTest {

    private class RandomNumberProducer(private val range: Int) : Producer<Int, RuntimeException> {
        private val random = Random(0)
        private var produced = 0 // to mimic failure for every other call
        override fun produce() =
            if (produced++ % 2 == 0) Producible.Result(random.nextInt(range))
            else Producible.Failure(IllegalArgumentException("Failed to produce a random number"))

    }

    @Test
    fun `Identity Test`() {
        val producer = RandomNumberProducer(100)

        producer.produce().also { actual ->
            assertInstanceOf<Producible.Result<Int>>(actual)
            assertTrue(actual.value < 100)
            assertTrue(actual.success)
            // Functor identity test for Monad
            assertEquals(actual, actual.map { it })
        }

        producer.produce().also { actual ->
            assertInstanceOf<Producible.Failure<RuntimeException>>(actual)
            assertInstanceOf<IllegalArgumentException>(actual.explanation)
            assertEquals("Failed to produce a random number", actual.explanation.message)
            assertFalse(actual.success)
            // Functor identity test for Monad
            assertEquals(actual, actual.map { it })
        }
    }

    @Test
    fun `Map Composition Test`() {
        val producer = RandomNumberProducer(100)
        val f: (Int) -> Double = { it / 2.0 }
        val g: (Double) -> String = { String.format("%.2f D", it) }

        val result = producer.produce()
        result.map(f).map(g).also { actual ->
            assertEquals(result.map { g(f(it)) }, actual) // Functor composition test for Monad - Result
            assertInstanceOf<Producible.Result<String>>(actual)
            assertTrue(actual.value.endsWith(".00 D"))
        }

        val failure = producer.produce()
        failure.map(f).map(g).also { actual ->
            assertEquals(failure.map { g(f(it)) }, actual) // Functor composition test for Monad - Failure
            assertInstanceOf<Producible.Failure<RuntimeException>>(actual)
            assertInstanceOf<IllegalArgumentException>(actual.explanation)
            assertEquals("Failed to produce a random number", actual.explanation.message)
        }
    }
}
