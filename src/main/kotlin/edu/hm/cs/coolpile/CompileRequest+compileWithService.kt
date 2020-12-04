package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.config.AvailableService
import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import edu.hm.cs.coolpile.exception.CompilationException
import org.springframework.util.Base64Utils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun CompileRequest.compileWithService(service: AvailableService): CompileResult {
    val timestampBefore = System.currentTimeMillis()

    val sourceCode = String(Base64Utils.decodeFromString(sourceCode), Charsets.UTF_8)

    println("Compile job started.")

    File("${CompileController.tempCompileFileName}${service.fileSuffix}").writeText(sourceCode)

    val compilationProcess = Runtime.getRuntime().exec(service.cmd)
    println("Waiting for compiling job to finish...")

    val exitValue = compilationProcess.waitFor()

    if (exitValue == 0) {
        println("Done.")
    } else {
        println("Error compiling file.")

        val errorMessage = try {
            BufferedReader(InputStreamReader(compilationProcess.errorStream)).use { reader ->
                reader.readLine()
            }
        } catch (e: IOException) {
            e.stackTraceToString()
        }

        println(errorMessage)

        throw CompilationException(errorMessage)
    }

    val assemblyFile = File("${CompileController.tempCompileFileName}${service.outputFileSuffix}")
    val assembly = assemblyFile.readText(Charsets.UTF_8)

    assemblyFile.delete()
    File("${CompileController.tempCompileFileName}.c").delete()

    return CompileResult(
            assembly = Base64Utils.encodeToString(assembly.toByteArray()),
            compilationTime = (System.currentTimeMillis() - timestampBefore).milliseconds.toString()
    )
}
