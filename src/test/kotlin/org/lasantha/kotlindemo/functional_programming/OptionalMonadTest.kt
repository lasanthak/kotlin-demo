package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class OptionalMonadTest {

    @Test
    fun `Identity Test`() {
        data class MyData(val id: Int, val name: String)

        Optional.of(MyData(11, "John Doe")).also { actual ->
            // Identity test for creation
            assertEquals(actual, Optional.of(MyData(11, "John Doe")))
            // Identity test for Monad
            assertEquals(actual, actual.flatMap { Optional.of(it) })
            // Functor identity test for Monad
            assertEquals(actual, actual.map { it })
        }

        Optional.of(null).also { actual ->
            // Identity test for creation
            assertEquals(actual, Optional.of(null))
            // Identity test for Monad
            assertEquals(actual, actual.flatMap { Optional.of(it) })
            // Functor identity test for Monad
            assertEquals(actual, actual.map { it })
        }
    }

    @Test
    fun `Composition Test`() {
        data class MyData(val name: String, val age: Int)

        val regex = """[^.]* name is (\w+)[^.]* (\d+) years old[^.]*\.""".toRegex()

        val f: (String) -> MyData = {
            val result = regex.find(it)
            if (result != null) {
                val (name, age) = result.destructured
                MyData(name, age.toInt())
            } else {
                throw IllegalArgumentException("Invalid input")
            }
        }

        val g: (MyData) -> String = { "We found ${it.name} who is ${it.age} years old." }

        val string = "My name is Merlin, and I am 178 years old."
        Optional.of(string).map(f).map(g).also { actual ->
            assertTrue(actual is Optional.Some)
            assertEquals("We found Merlin who is 178 years old.", (actual as Optional.Some).value)
        }

        // Functor composition test for Monad
        assertEquals(
            Optional.of(g(f(string))),
            Optional.of(string).map(f).map(g)
        )
        assertEquals(
            Optional.None,
            Optional.of(null).map(f).map(g)
        )
    }


    @Test
    fun `FlatMap Composition Test`() {
        data class MyData(val name: String, val age: Int)

        val regex = """[^.]* name is (\w+)[^.]* (\d+) years old[^.]*\.""".toRegex()

        val f: (String?) -> Optional<MyData> = { s ->
            val result = s?.let { regex.find(it) }
            when {
                result == null -> Optional.None
                else -> {
                    val (name, age) = result.destructured
                    Optional.of(MyData(name, age.toInt()))
                }
            }
        }

        val g: (MyData?) -> Optional<String> = {
            when (it) {
                null -> Optional.of("We did not find anyone.")
                else -> Optional.of("We found ${it.name} who is ${it.age} years old.")
            }
        }

        val string = "My name is Merlin, and I am 178 years old."
        // Composition test for Monad flatMap
        assertEquals(
            g((f(string) as Optional.Some).value),
            Optional.of(string).flatMap(f).flatMap(g)
        )
        assertEquals(
            Optional.None, //TODO - How do we get Optional.of("We did not find anyone.") ?
            Optional.of(null).flatMap(f).flatMap(g)
        )
    }

}
