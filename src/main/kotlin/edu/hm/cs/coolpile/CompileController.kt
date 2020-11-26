package edu.hm.cs.coolpile

import org.springframework.http.HttpStatus
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


@RestController
class CompileController {

    @PostMapping("/compile")
    fun compile(@RequestBody compileRequest: CompileRequest): CompileRequest {

        val sourceCode = String(Base64Utils.decodeFromString(compileRequest.sourceCode), Charsets.UTF_8)

        println("Source Code:\n\n${sourceCode.prependIndent("| ")}\n")

        File("$tempCompileFileName.c").writeText(sourceCode)

        val compilationProcess = Runtime.getRuntime().exec("gcc -S $tempCompileFileName.c")
        println("Waiting for compiling job finishes...")

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

        println("\nCompilation:\n\n${assembly.prependIndent("| ")}\n")

        return CompileRequest(Base64Utils.encodeToString(assembly.toByteArray()))
    }

    companion object {
        const val tempCompileFileName = "temp"
    }
}