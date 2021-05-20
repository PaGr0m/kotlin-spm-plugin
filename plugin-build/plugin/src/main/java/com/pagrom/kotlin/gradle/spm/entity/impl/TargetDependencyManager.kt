package com.pagrom.kotlin.gradle.spm.entity.impl

import com.pagrom.kotlin.gradle.spm.entity.TargetDependency
import com.pagrom.kotlin.gradle.spm.entity.TargetDependencyMarker

@TargetDependencyMarker
class TargetDependencyManager {
    val targetDependencies = mutableListOf<TargetDependency>()

    fun target(name: String, condition: String? = null) {
        val target = Target(name, condition)
        targetDependencies.add(target)
    }

    fun product(name: String, dependency: String, condition: String? = null) {
        val product = Product(name, dependency, condition)
        targetDependencies.add(product)
    }

    data class Target(
        private val name: String,
        val condition: String? = null
    ) : TargetDependency {
        override fun getName(): String = name
    }

    data class Product(
        private val name: String,
        val dependency: String,
        val condition: String? = null
    ) : TargetDependency {
        override fun getName(): String = name
    }
}
