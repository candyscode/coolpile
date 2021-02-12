package edu.hm.cs.coolpile.model.config

import com.beust.klaxon.Klaxon
import edu.hm.cs.coolpile.model.compiler.CompilerService
import edu.hm.cs.coolpile.model.dto.ConfigurationModel
import java.io.File

class ServiceConfiguration(configFile: File) {
    val compilers: List<CompilerService>

    init {
        val configText = configFile.readText()
        val serviceGroup = Klaxon().parse<ConfigurationModel>(configText)
                ?: throw IllegalArgumentException("Exception occurred during parsing of Config File.")

        compilers = serviceGroup.compilers.toList()

        compilers.forEach {
            compilerService -> compilerService.initialize()
        }
    }
}