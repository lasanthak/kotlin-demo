package org.lasantha.kotlindemo.functional_programming

/**
 * Functors:
 * Let C and D be categories. A functor F from C to D is a mapping that:
 *  - associates with each object X in C an object F(X) in D
 *  - associates with each morphism f: X -> Y in C and a morphism F(f): F(X) -> F(Y) in D
 *  such that the following two conditions hold:
 *    1. F(id_X) = id_F(X) for every object X in C
 *    2. F(g ∘ f) = F(g) ∘ F(f) for all morphisms f: X -> Y and g: Y -> Z in C
 *
 * Functor laws:
 * 1. Functors must preserve identity morphisms
 * 2. Functors must preserve composition of morphisms
 *
 * Example:
 * A simple class to demonstrate the functor laws.
 *
 * data class Triplet<A>(val a: A, val b: A, val c: A) : Functor<A> {
 *     override fun <B> map(f: (A) -> B): Triplet<B> {
 *         return Triplet(f(a), f(b), f(c))
 *     }
 * }
 */
interface Functor<A> {
    fun <B> map(f: (A) -> B): Functor<B>
}


/**
 * A simple class to demonstrate the functor laws.
 */
data class Triplet<A>(val a: A, val b: A, val c: A) : Functor<A> {
    override fun <B> map(f: (A) -> B): Triplet<B> {
        return Triplet(f(a), f(b), f(c))
    }
}
