package com.pagrom.kotlin.gradle.spm.utils

@Suppress("UnnecessaryAbstractClass")
abstract class StringUnaryPlusContainer {
    val container = mutableListOf<String>()

    operator fun String.unaryPlus() {
        container.add(this)
    }
}
