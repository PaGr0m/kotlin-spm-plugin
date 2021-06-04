package com.github.pagr0m.kotlin.native.spm.entity.impl

import com.github.pagr0m.kotlin.native.spm.entity.Product
import com.github.pagr0m.kotlin.native.spm.entity.ProductMarker
import com.github.pagr0m.kotlin.native.spm.utils.StringUnaryPlusContainer
import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

@ProductMarker
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
