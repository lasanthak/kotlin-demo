package org.lasantha.kotlindemo.nullcheck

class Foo {


    // This function is expected to return a non-null value.
    fun bar(): String {
        val map = mutableMapOf("foo" to "bar")
        val r1 = map.getOrDefault("foo", null)
        return r1!!
    }


}
