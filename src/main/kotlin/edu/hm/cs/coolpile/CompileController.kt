package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.config.CompilationService
import edu.hm.cs.coolpile.config.ServiceConfiguration
import edu.hm.cs.coolpile.config.ServiceDescription
import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import kotlin.time.ExperimentalTime

@ExperimentalTime
@RestController
class CompileController {

    @PostMapping("/compiler/**")
    fun serviceWildcard(request: HttpServletRequest, @RequestBody compileRequest: CompileRequest): CompileResult {
        val apiEndpoint = request.requestURI.removePrefix("/compiler/").throwIfSubPathIsEmpty()
        return compileRequest.compileInContainer(
                service = configuration.getServiceByName(apiEndpoint),
                sessionId = request.session.id
        )
    }

    @GetMapping("/compiler")
    fun services(): List<ServiceDescription> =
            configuration.services.map { ServiceDescription(it.name, it.description) }

    @GetMapping("/services")
    fun getServices(): Array<CompilationService> {
        return configuration.services
    }

    @PostMapping("/services/**")
    fun addService(
            request: HttpServletRequest,
            @RequestBody compilationService: CompilationService,
    ): Array<CompilationService> {
        val apiEndpoint = request.requestURI.removePrefix("/services/").throwIfSubPathIsEmpty()
        if (apiEndpoint != compilationService.name)
            throw IllegalArgumentException("Service definition name doesn't match with accessed endpoint")

        compilationService.createDockerImageIfNecessary()
        configuration = ServiceConfiguration(configuration.services + compilationService)
        return configuration.services
    }

    @DeleteMapping("/services/**")
    fun deleteService(
            request: HttpServletRequest,
            @RequestBody serviceName: String
    ): Array<CompilationService> {
        val apiEndpoint = request.requestURI.removePrefix("/services/").throwIfSubPathIsEmpty()
        val x = configuration.getServiceByName(apiEndpoint)
        x.createDockerImageIfNecessary()
        configuration = ServiceConfiguration(configuration.services.toMutableList().minus(x).toTypedArray())

        return configuration.services
    }

    private fun String.throwIfSubPathIsEmpty(): String {
        if (this == "/services") {
            throw IllegalArgumentException("No explicit endpoint specified.")
        } else {
            return this
        }
    }
}
