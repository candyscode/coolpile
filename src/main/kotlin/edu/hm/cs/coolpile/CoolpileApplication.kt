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
    if (!hostIsCompatible()) return

    val configFile = File(args[0]).readText()
    configuration = Klaxon().parse<ServiceConfiguration>(configFile)
            ?: throw IllegalArgumentException("Exception occurred during parsing of Config File.")

    runApplication<CoolpileApplication>(*args)
}

private fun hostIsCompatible(): Boolean {
    val compilationProcess = Runtime.getRuntime().exec("docker -v")

    return when (compilationProcess.waitFor()) {
        0 -> true
        127 -> {
            println("Docker is not installed on this machine. Install Docker and run this command again.")
            false
        }
        else -> {
            println("There is a problem with your Docker installation. Coolpile cannot be started.")
            false
        }
    }
}
