package edu.hm.cs.coolpile.model.compiler

import edu.hm.cs.coolpile.model.dto.CompileResult
import edu.hm.cs.coolpile.model.compiler.CompilerService.ImageType.INSTALL
import edu.hm.cs.coolpile.model.compiler.CompilerService.ImageType.PROVIDED
import edu.hm.cs.coolpile.util.runOnHost
import org.springframework.util.Base64Utils
import java.io.File

class CompilerService(
        val name: String,
        val description: String,
        val image: String,
        val cmd: String,
        val params: String,
        val inputFileSuffix: String,
        val outputFileSuffix: String,
) {
    fun initialize() {
        val imageSpecifier = image.toImage()
        when (image.imageType) {
            INSTALL -> installImage(imageSpecifier)
            PROVIDED -> checkImage(imageSpecifier)
        }
    }

    fun compile(sourceCode: String, sessionId: String): CompileResult {
        val timestampBefore = System.currentTimeMillis()
        val decodedSourceCode = String(Base64Utils.decodeFromString(sourceCode))

        File("$sessionId$inputFileSuffix").writeText(decodedSourceCode)

        val command = arrayOf(
                compileCommand,
                sessionId,
                name,
                cmd,
                params,
                inputFileSuffix,
                outputFileSuffix
        )

        val errorStreamText = runOnHost(command).replace(sessionId, sessionIdReplacement)

        val assemblyFile = File("$sessionId$outputFileSuffix")
        val assembly = assemblyFile.readText(Charsets.UTF_8)

        assemblyFile.delete()

        File("$sessionId$inputFileSuffix").delete()

        return CompileResult(
                assembly = assembly.toBase64(),
                compilationTime = (System.currentTimeMillis() -
                        timestampBefore).toString() + timeSuffix,
                errorStream = errorStreamText.toBase64()
        )
    }

    private fun installImage(imageSpecifier: String) {
        runOnHost("$inspectCommand $installImageNamePrefix$name") {
            println("Docker image for service $name doesn't exist and will be created now.")

            val command = arrayOf(imageCreationCommand, name, imageSpecifier)
            runOnHost(command) { throw IllegalStateException("Error while creating docker image.") }
        }
    }

    private fun checkImage(imageSpecifier: String) =
            runOnHost("$inspectCommand $imageSpecifier") {
                throw IllegalStateException("Couldn't find Docker image $imageSpecifier")
            }

    private val String.imageType: ImageType
        get() = when {
            startsWith(installPrefix) -> INSTALL
            startsWith(providedPrefix) -> PROVIDED
            else -> throw IllegalArgumentException("Unexpected image type for compiler service.")
        }

    private fun String.toImage(): String {
        val imageSpecifier = removePrefix("${imageType.name}:")
        if (imageSpecifier.length >= length) {
            throw IllegalArgumentException("Type prefix could not be removed.")
        }
        return imageSpecifier
    }

    private fun String.toBase64() = Base64Utils.encodeToString(toByteArray())

    private enum class ImageType { INSTALL, PROVIDED }

    companion object {
        private const val inspectCommand = "docker image inspect"
        private const val installImageNamePrefix = "coolpile-"
        private const val imageCreationCommand = "./src/main/shell/createDockerImage.sh"
        private const val compileCommand = "./src/main/shell/compile.sh"
        private const val timeSuffix = " ms"
        private const val sessionIdReplacement = "YOURFILE"
        private const val installPrefix = "INSTALL:"
        private const val providedPrefix = "PROVIDED:"
    }
}