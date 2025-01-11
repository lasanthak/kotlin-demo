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
 */
interface Functor<out A> {
    fun <B> map(f: (A) -> B): Functor<B>
}



/**
 * A monad can be thought of as a container type that supports two operations: map and flatMap.
 * A monad is a functor which can be created by defining a type constructor M and two operations:
 *   unit (or pure) : a -> M a ;which receives a value of type a and wraps it into a monadic value of type M a, and
 *   flatMap (or bind) : (M a) -> (a -> M b) -> (M b) ;which receives a monadic value M a and a function f
 * that accepts values of the base type a. This unwraps a, applies f to it, and can process the result of f
 * as a monadic value M b.
 * Because monads are functors, they must also obey the functor laws and support map function
 *   map : (M a) -> (a -> b) -> (M b) ;which applies a function to the value inside the monad.
 */
interface Monad<out A> : Functor<A> {
    override fun <B> map(f: (A) -> B): Monad<B>

    fun <B> flatMap(f: (A) -> Monad<B>): Monad<B>
}
