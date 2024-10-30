package org.lasantha.kotlindemo.functional_programming

interface Monad<out A> {

    fun <B> map(f: (A) -> B): Monad<B>

    fun <B> flatMap(f: (A) -> Monad<B>): Monad<B>
}
