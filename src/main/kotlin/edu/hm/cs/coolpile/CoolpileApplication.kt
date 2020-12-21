package edu.hm.cs.coolpile

import com.beust.klaxon.Klaxon
import edu.hm.cs.coolpile.config.AvailableService
import edu.hm.cs.coolpile.config.ServiceConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

@SpringBootApplication
class CoolpileApplication

lateinit var configuration: ServiceConfiguration

fun main(args: Array<String>) {
    if (!hostIsCompatible()) return

    val configFile = File(args[0]).readText()
    configuration = Klaxon().parse<ServiceConfiguration>(configFile)
            ?: throw IllegalArgumentException("Exception occurred during parsing of Config File.")

    println("Configuration import successful.")

    configuration.services.toList().forEach { it.createDockerImageIfNecessary() }

    runApplication<CoolpileApplication>(*args)
}

private fun AvailableService.createDockerImageIfNecessary() {
    val returnCode = Runtime.getRuntime().exec("docker image inspect coolpile-$name").waitFor()
    if (returnCode != 0) {
        println("Docker image for service $name doesn't exist and will be created now.")

        val command = arrayOf("./src/main/shell/createDockerImage.sh", name, install)
        Runtime.getRuntime().exec(command).waitFor()
    }
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
