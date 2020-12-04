package edu.hm.cs.coolpile

import com.beust.klaxon.Klaxon
import edu.hm.cs.coolpile.config.ServiceConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class CoolpileApplication

lateinit var configuration: ServiceConfiguration

fun main(args: Array<String>) {
    val configFile = File(args[0]).readText()
    configuration = Klaxon().parse<ServiceConfiguration>(configFile)
            ?: throw IllegalArgumentException("Exception occurred during parsing of Config File.")

    runApplication<CoolpileApplication>(*args)
}
