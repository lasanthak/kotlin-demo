package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test



class FunctorTest {
    @Test
    fun `Identity Test`() {
        // Functors must preserve identity morphisms
        val t1 = Triplet(1, 2, 3)
        val t2 = t1.map { it } // F(id_X)
        assertEquals(t1, t2)
    }

    @Test
    fun `Composition Test`() {
        // Functors must preserve composition of morphisms
        val f:(Int) -> Double = { it / 2.0 }
        val g:(Double) -> String = { "${it}D" }
        val t = Triplet(1, 2, 3)
        val t1 = t.map { g(f(it)) } // F(g ∘ f)
        val t2 = t.map(f).map(g) // F(g) ∘ F(f)
        assertEquals(t1, t2)
    }
}
