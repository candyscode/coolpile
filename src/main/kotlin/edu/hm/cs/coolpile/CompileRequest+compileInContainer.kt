package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.config.AvailableService
import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import org.springframework.util.Base64Utils
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun CompileRequest.compileInContainer(sessionId: String, service: AvailableService): CompileResult {
    val timestampBefore = System.currentTimeMillis()

    val sourceCode = String(Base64Utils.decodeFromString(sourceCode), Charsets.UTF_8)
    File("$sessionId.c").writeText(sourceCode)

    val command = arrayOf(
            "./src/main/shell/compile.sh",
            sessionId,
            service.name,
            service.cmd,
            service.params,
            service.inputFileSuffix,
            service.outputFileSuffix
    )

    Runtime.getRuntime().exec(command).waitFor()

    val assemblyFile = File("$sessionId.s")
    val assembly = assemblyFile.readText(Charsets.UTF_8)

    assemblyFile.delete()
    File("$sessionId.c").delete()

    return CompileResult(
            assembly = Base64Utils.encodeToString(assembly.toByteArray()),
            compilationTime = (System.currentTimeMillis() - timestampBefore).milliseconds.toString()
    )
}
