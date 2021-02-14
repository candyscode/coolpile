package edu.hm.cs.coolpile.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

fun runOnHost(command: String, onErrorAction: () -> Unit = {}) =
        Runtime.getRuntime().exec(command).runOnHost(onErrorAction)

fun runOnHost(command: Array<String>, onErrorAction: () -> Unit = {}) =
        Runtime.getRuntime().exec(command).runOnHost(onErrorAction)

private fun Process.runOnHost(onErrorAction: () -> Unit): String {
    val returnCode = waitFor()

    val stdError = BufferedReader(InputStreamReader(errorStream))

    val errorString = stdError.lines().collect(Collectors.joining(","))
    if (errorString.isNotEmpty()) println(errorString)

    if (returnCode != 0) onErrorAction()

    return errorString
}