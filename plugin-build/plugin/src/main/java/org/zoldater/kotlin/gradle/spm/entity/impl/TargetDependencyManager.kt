/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.spm.entity.impl

import org.jetbrains.kotlin.gradle.plugin.spm.entity.TargetDependency
import org.jetbrains.kotlin.gradle.plugin.spm.entity.impl.TargetDependencyManager.Target

/**
 * @see [Target.Dependency](https://github.com/apple/swift-package-manager/blob/main/Documentation/PackageDescription.md#target-dependency)
 * @see [Target.Dependency](https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#target-dependency)
 */
class TargetDependencyManager {
    val targetDependencies = mutableListOf<TargetDependency>()

    fun target(name: String, condition: String? = null) {
        val target = Target(name, condition)
        targetDependencies.add(target)
    }

    fun product(name: String, `package`: String, condition: String? = null) {
        val product = Product(name, `package`, condition)
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
        val `package`: String,
        val condition: String? = null
    ) : TargetDependency {
        override fun getName(): String = name
    }
}
