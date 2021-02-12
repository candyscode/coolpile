package edu.hm.cs.coolpile.util

fun runOnHost(command: String, onErrorAction: () -> Unit = {}) {
    val returnCode = Runtime.getRuntime().exec(command).waitFor()
    if (returnCode != 0) onErrorAction()
}

fun runOnHost(command: Array<String>, onErrorAction: () -> Unit = {}) {
    val returnCode = Runtime.getRuntime().exec(command).waitFor()
    if (returnCode != 0) onErrorAction()
}