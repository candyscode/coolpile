package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.config.AvailableService
import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import org.springframework.util.Base64Utils
import java.io.File

// TODO: Think of way to handle different services (RISCV, JAVA, ...)
fun CompileRequest.compileInContainer(sessionId: String, service: AvailableService): CompileResult {
    service.createDockerImageIfNecessary()

    val sourceCode = String(Base64Utils.decodeFromString(sourceCode), Charsets.UTF_8)
    File("$sessionId.c").writeText(sourceCode)

    Runtime.getRuntime().exec("./src/main/shell/compile.sh $sessionId").waitFor()

    val assemblyFile = File("$sessionId.s")
    val assembly = assemblyFile.readText(Charsets.UTF_8)

    assemblyFile.delete()
    File("$sessionId.c").delete()

    // TODO: Calculate execution time
    return CompileResult(
            assembly = Base64Utils.encodeToString(assembly.toByteArray()),
            compilationTime = "nope"
    )
}

private fun AvailableService.createDockerImageIfNecessary() {
    val returnCode = Runtime.getRuntime().exec("docker image inspect coolpile-$name").waitFor()
    if (returnCode != 0) {
        Runtime.getRuntime().exec("./src/main/shell/createDockerImage.sh \"$name\" \"$install\"").waitFor()
    }
}
