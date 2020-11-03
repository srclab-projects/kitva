@file:JvmName("Hash")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.util.*

fun Any?.hash(): Int {
    return this.hashCode()
}

fun Any?.arrayHash(): Int {
    return when (this) {
        null -> this.hashCode()
        is BooleanArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.hashCode(this)
        else -> this.hashCode()
    }
}

fun Any?.arrayDeepHash(): Int {
    return when (this) {
        null -> this.hashCode()
        is BooleanArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.deepHashCode(this)
        else -> this.hashCode()
    }
}

fun hash(vararg args: Any?): Int {
    return args.contentHashCode()
}

fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}