package com.pagrom.kotlin.gradle.spm.plugin

import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.*

internal fun <T> assertArrayEquals(expected: List<T>, actual: List<T>) = assertTrue(
    expected.size == actual.size &&
    expected.containsAll(actual) &&
    actual.containsAll(expected)
)

internal fun checkTasksOutcome(tasks: List<BuildTask>, expectedOutcome: TaskOutcome) = tasks.forEach {
    assertNotNull(it)
    assertEquals(expectedOutcome, it.outcome)
}
