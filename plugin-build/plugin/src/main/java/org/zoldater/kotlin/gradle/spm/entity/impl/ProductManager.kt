package org.zoldater.kotlin.gradle.spm.entity.impl

import groovy.lang.Closure
import org.gradle.util.ConfigureUtil
import org.zoldater.kotlin.gradle.spm.entity.Product
import org.zoldater.kotlin.gradle.spm.utils.StringUnaryPlusContainer

/**
 * @see [Product](https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#product)
 * @see [Product](https://github.com/apple/swift-package-manager/blob/main/Documentation/PackageDescription.md#product)
 */
class ProductManager {
    val products = mutableListOf<Product>()

    fun library(name: String, type: Library.LibraryType, configure: Library.() -> Unit) {
        val library = Library(name, type).apply(configure)
        products.add(library)
    }

    fun library(name: String, type: Library.LibraryType, configure: Closure<*>) = library(name, type) {
        ConfigureUtil.configure(configure, this)
    }

    fun executable(name: String, configure: Executable.() -> Unit) {
        val executable = Executable(name).apply(configure)
        products.add(executable)
    }

    fun executable(name: String, configure: Closure<*>) = executable(name) {
        ConfigureUtil.configure(configure, this)
    }

    class Executable(name: String) : AbstractProduct(name)

    class Library(name: String, private val type: LibraryType) : AbstractProduct(name) {
        enum class LibraryType {
            STATIC,
            DYNAMIC
        }
    }

    abstract class AbstractProduct(
        private val name: String
    ) : StringUnaryPlusContainer(), Product {
        override fun getName(): String = name
    }
}
