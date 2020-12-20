package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import org.springframework.util.Base64Utils
import java.io.File

// TODO: Think of way to handle different services (RISCV, JAVA, ...)
fun CompileRequest.compileInContainer(sessionId: String): CompileResult {
    val sourceCode = String(Base64Utils.decodeFromString(sourceCode), Charsets.UTF_8)

    println("Compile job started.")

    File("$sessionId.c").writeText(sourceCode)

    val compilationProcess = Runtime.getRuntime().exec("./src/main/bash/compile.sh")
    val exitValue = compilationProcess.waitFor()

    println("Exited with value $exitValue")

    val assemblyFile = File("$sessionId.s")
    val assembly = assemblyFile.readText(Charsets.UTF_8)

    assemblyFile.delete()
    File("$sessionId.c").delete()

    return CompileResult(
            assembly = Base64Utils.encodeToString(assembly.toByteArray()),
            compilationTime = "nope"
    )
}