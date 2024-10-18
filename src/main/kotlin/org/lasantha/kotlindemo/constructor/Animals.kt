package org.lasantha.kotlindemo.constructor

interface Animal

data class Cat(val type:String): Animal {
    val tabby = type.contains("shorthair", ignoreCase = true)

    val american = when(type.toCharArray()[0]) {
        'A', 'a' -> true
        else -> false
    }
}
