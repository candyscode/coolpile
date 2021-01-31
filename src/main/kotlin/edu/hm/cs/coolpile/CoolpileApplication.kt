// SERVICE HINZUFUEGEN during runtime (AUTH) X
// Konzept (viele Bilder) und evaluierung kapitel hinzufügen
// Starten von vorhandenem docker img anstelle von install
// Webservices Chapter: Warum kein Cluster System => Client flexibel halten, einfach einbindbar
// Demo Client

package edu.hm.cs.coolpile

import com.beust.klaxon.Klaxon
import edu.hm.cs.coolpile.config.CompilationService
import edu.hm.cs.coolpile.config.ServiceConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.lang.IllegalStateException

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

fun CompilationService.createDockerImageIfNecessary() {
    when {
        imageIsInstallable() -> {
            val returnCode = Runtime.getRuntime().exec("docker image inspect coolpile-$name").waitFor()
            if (returnCode != 0) {
                println("Docker image for service $name doesn't exist and will be created now.")

                val installCommand = image.removePrefix("INSTALL:")
                val command = arrayOf("./src/main/shell/createDockerImage.sh", name, installCommand)
                Runtime.getRuntime().exec(command).waitFor()
            }
        }
        imageIsProvided() -> {
            val imageName = image.removePrefix("PROVIDED:")
            val returnCode = Runtime.getRuntime().exec("docker image inspect $imageName}").waitFor()
            if (returnCode != 0) {
                throw IllegalStateException("Couldn't find Docker image $imageName")
            }
        }
        else -> {
            throw IllegalStateException("Invalid Image reference")
        }
    }
}

private fun CompilationService.imageIsInstallable() = image.startsWith("INSTALL:")
private fun CompilationService.imageIsProvided() = image.startsWith("PROVIDED:")

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
