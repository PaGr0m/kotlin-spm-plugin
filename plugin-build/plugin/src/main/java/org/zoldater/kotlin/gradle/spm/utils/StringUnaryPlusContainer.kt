package org.zoldater.kotlin.gradle.spm.utils

abstract class StringUnaryPlusContainer {
    val container = mutableListOf<String>()

    operator fun String.unaryPlus() {
        container.add(this)
    }
}
