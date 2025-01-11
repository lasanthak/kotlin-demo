package org.lasantha.kotlindemo.functional_programming

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TripletTest {
    @Test
    fun `Identity Test`() {
        // Functors must preserve identity morphisms
        // F(id_X) = id_F(X)
        val f_id = { it: Int -> it }
        val id_f = { it: Triplet<Int> -> it }

        val t = Triplet(1, 2, 3)
        val t1 = id_f(t)
        val t2 = t.map(f_id) // similar to .map { it }
        assertEquals(t1, t2)
    }

    @Test
    fun `Composition Test`() {
        // Functors must preserve composition of morphisms
        // F(g ∘ f) = F(g) ∘ F(f)
        val f: (Double) -> Int = { (it * 100.00).toInt() }
        val g: (Int) -> String = { "${it}%" }
        val t = Triplet(0.5, 0.99, 0.36)
        val t1 = t.map { g(f(it)) } // F(g ∘ f)
        val t2 = t.map(f).map(g) // F(g) ∘ F(f)
        assertEquals(t1, t2)
    }

    @Test
    fun `Composition Test 2`() {
        // Associative type composition
        // F(h ∘ g ∘ f) = F(h ∘ g) ∘ F(f) = F(h) ∘ F(g ∘ f)
        val f: (Long) -> Double = { it.toDouble() }
        val g: (Double) -> Int = { (it * 3.33).toInt() }
        val h: (Int) -> String = { "<${it}>" }

        val t = Triplet(4L, 96L, 45L)
        val t1 = t.map(f).map { h(g(it)) }
        val t2 = t.map { g(f(it)) }.map(h)
        val t3 = t.map(f).map(g).map(h)
        println(t3)
        assertEquals(t1, t3)
        assertEquals(t2, t3)
    }

    @Test
    fun `Unit test`() {
        assertEquals(Triplet(1, 2, 3), Triplet(1, 2, 3))
        assertNotEquals(Triplet(1, 2, 3), Triplet(1, 2, 4))
    }
}
