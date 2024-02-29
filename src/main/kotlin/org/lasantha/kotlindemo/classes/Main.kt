package org.lasantha.kotlindemo.classes

import org.lasantha.kotlindemo.classes.model.Cat

fun main(args: Array<String>) {
    printIt("My cat:", Cat("American Shorthair"))
    printIt("Next door cat:", Cat("Persian"))
    printIt("Wild cat:", Cat("a puma"))
}

private fun printIt(msg: String, cat: Cat) {
    println("$msg $cat, tabby=${cat.tabby}, american=${cat.american}")
}