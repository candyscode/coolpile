// SERVICE HINZUFUEGEN during runtime (AUTH) X
// Konzept (viele Bilder) und evaluierung kapitel hinzufügen
// Starten von vorhandenem docker img anstelle von install X
// Webservices Chapter: Warum kein Cluster System => Client flexibel halten, einfach einbindbar
// Demo Client

package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.model.config.ServiceConfiguration
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.io.File

@SpringBootApplication
class CoolpileApplication(private val applicationArguments: ApplicationArguments) {
    
    @Bean
    fun serviceConfiguration(): ServiceConfiguration {
        if(applicationArguments.sourceArgs.isEmpty()) {
            return ServiceConfiguration(null)
        }

        return ServiceConfiguration(File(applicationArguments.sourceArgs[0]))
    }
}

fun main(args: Array<String>) {
    if (!hostIsCompatible()) return
    runApplication<CoolpileApplication>(*args)
}


private fun hostIsCompatible(): Boolean {
    return try {
        Runtime.getRuntime().exec("docker -v").waitFor()
        Runtime.getRuntime().exec("docker info").waitFor()
        true
    } catch (e: Exception) {
        println("There is an issue with the docker installation on this host: $e")
        false
    }
}
