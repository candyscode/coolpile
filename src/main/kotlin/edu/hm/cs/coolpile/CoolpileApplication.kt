package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.model.config.ServiceConfiguration
import edu.hm.cs.coolpile.util.hostIsCompatible
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
