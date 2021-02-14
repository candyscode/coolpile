package edu.hm.cs.coolpile.model.config

import com.beust.klaxon.Klaxon
import edu.hm.cs.coolpile.model.compiler.CompilerService
import edu.hm.cs.coolpile.model.dto.ConfigurationModel
import java.io.File
import java.lang.IllegalStateException

class ServiceConfiguration(configFile: File) {
    var compilers: List<CompilerService>
        private set

    init {
        val configText = configFile.readText()
        val serviceGroup = Klaxon().parse<ConfigurationModel>(configText)
                ?: throw IllegalArgumentException("Exception occurred during parsing of Config File.")

        compilers = serviceGroup.compilers.toList()

        compilers.forEach {
            compilerService -> compilerService.initialize()
        }
    }

    fun addCompiler(compiler: CompilerService): List<CompilerService> {
        compiler.initialize()
        compilers = compilers + compiler
        return compilers
    }

    fun removeCompiler(compilerName: String): List<CompilerService> {
        val oldListSize = compilers.size
        val targetCompiler = compilers.firstOrNull {
            compilerService -> compilerService.name == compilerName
        } ?: throw IllegalArgumentException("Compiler could not be found for deletion.")

        compilers = compilers - targetCompiler
        if (oldListSize >= compilers.size) {
            throw IllegalStateException("Compiler $compilerName could not be removed.")
        }
        return compilers
    }
}