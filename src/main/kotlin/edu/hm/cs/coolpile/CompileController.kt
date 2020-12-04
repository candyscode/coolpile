package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.config.AvailableService
import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import edu.hm.cs.coolpile.exception.CompilationException
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import javax.servlet.http.HttpServletRequest
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@RestController
class CompileController {

    @PostMapping("/services/**")
    fun services(request: HttpServletRequest, @RequestBody compileRequest: CompileRequest): CompileResult {
        val apiEndpoint = request.requestURI.removePrefix("/services/")
        return compileRequest.compileWithService(configuration.getServiceByName(apiEndpoint))
    }

    private fun CompileRequest.compileWithService(service: AvailableService): CompileResult {
        val timestampBefore = System.currentTimeMillis()

        val sourceCode = String(Base64Utils.decodeFromString(sourceCode), Charsets.UTF_8)

        println("Compile job started.")

        File("$tempCompileFileName${service.fileSuffix}").writeText(sourceCode)

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

        Thread.sleep(10000)

        val assemblyFile = File("$tempCompileFileName.s")
        val assembly = assemblyFile.readText(Charsets.UTF_8)

        assemblyFile.delete()
        File("$tempCompileFileName.c").delete()

        return CompileResult(
                assembly = Base64Utils.encodeToString(assembly.toByteArray()),
                compilationTime = (System.currentTimeMillis() - timestampBefore).milliseconds.toString()
        )
    }

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

            throw CompilationException(errorMessage)
        }

        Thread.sleep(10000)

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