package org.lasantha.kotlindemo.functional_programming

/**
 * A simple class to demonstrate the functor laws.
 */
data class Triplet<A>(val a: A, val b: A, val c: A) {
    fun <B> map(f: (A) -> B): Triplet<B> {
        return Triplet(f(a), f(b), f(c))
    }
}
