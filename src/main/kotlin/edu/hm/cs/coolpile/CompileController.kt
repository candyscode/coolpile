package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import edu.hm.cs.coolpile.exception.GCCException
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@RestController
class CompileController {

    @ExperimentalTime
    @PostMapping("/compile")
    fun compile(@RequestBody compileRequest: CompileRequest): CompileResult {

        val timestampBefore = System.currentTimeMillis()

        val sourceCode = String(Base64Utils.decodeFromString(compileRequest.sourceCode), Charsets.UTF_8)

        println("Compile job started.")
        // println("Source Code:\n\n${sourceCode.prependIndent("| ")}\n")

        File("$tempCompileFileName.c").writeText(sourceCode)

        val compilationProcess = Runtime.getRuntime().exec("gcc -S $tempCompileFileName.c")
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

            throw GCCException(errorMessage)
        }

        val assemblyFile = File("$tempCompileFileName.s")
        val assembly = assemblyFile.readText(Charsets.UTF_8)

        assemblyFile.delete()
        File("$tempCompileFileName.c").delete()

        // println("\nCompilation:\n\n${assembly.prependIndent("| ")}\n")

        return CompileResult(
                assembly = Base64Utils.encodeToString(assembly.toByteArray()),
                compilationTime = (System.currentTimeMillis() - timestampBefore).milliseconds.toString()
        )
    }

    companion object {
        const val tempCompileFileName = "temp"
    }
}