package org.lasantha.kotlindemo.functional_programming

/**
 * A monad can be thought of as a container type that supports two operations: map and flatMap.
 * A monad is a functor which can be created by defining a type constructor M and two operations:
 *   unit (or pure) : a -> M a ;which receives a value of type a and wraps it into a monadic value of type M a, and
 *   flatMap (or bind) : (M a) -> (a -> M b) -> (M b) ;which receives a monadic value M a and a function f
 * that accepts values of the base type a. Bind unwraps a, applies f to it, and can process the result of f
 * as a monadic value M b.
 * Because monads are functors, they must also obey the functor laws and support map function
 *  map : (M a) -> (a -> b) -> (M b) ;which applies a function to the value inside the monad.
 */
interface Monad<out A> {

    fun <B> map(f: (A) -> B): Monad<B>

    fun <B> flatMap(f: (A) -> Monad<B>): Monad<B>
}
