package edu.hm.cs.coolpile.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Runtime.getRuntime
import java.util.stream.Collectors

fun runOnHost(command: String, onErrorAction: () -> Unit = {}) =
        getRuntime().exec(command).runOnHost(onErrorAction)

fun runOnHost(command: Array<String>, onErrorAction: () -> Unit = {}) =
        getRuntime().exec(command).runOnHost(onErrorAction)

fun hostIsCompatible(): Boolean {
    if (System.getProperty("os.name").contains("windows", true)) {
        println("Coolpile seems to be run on a windows host. Right now only Unix systems are supported.")
        return false
    }
    if (!runsSuccessfully("docker info")) {
        println("Compatibility check failed. Make sure Docker is installed and running.")
        return false
    }
    println("Compatibility check OK.")
    return true
}

private fun Process.runOnHost(onErrorAction: () -> Unit): String {
    val returnCode = waitFor()

    val stdError = BufferedReader(InputStreamReader(errorStream))
    val errorString = stdError.lines().collect(Collectors.joining("\n"))

    if (returnCode != 0) onErrorAction()

    return errorString
}

private fun runsSuccessfully(command: String) = try {
    getRuntime().exec(command).waitFor() == 0
} catch (e: IOException) {
    false
}