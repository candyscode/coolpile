package edu.hm.cs.coolpile

import edu.hm.cs.coolpile.config.ServiceConfiguration
import edu.hm.cs.coolpile.dto.CompileRequest
import edu.hm.cs.coolpile.dto.CompileResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import kotlin.time.ExperimentalTime

@ExperimentalTime
@RestController
class CompileController {

    @PostMapping("/services/**")
    fun serviceWildcard(request: HttpServletRequest, @RequestBody compileRequest: CompileRequest): CompileResult {
        val apiEndpoint = request.requestURI.removePrefix("/services/").throwIfSubPathIsEmpty()
        return compileRequest.compileWithService(configuration.getServiceByName(apiEndpoint))
    }

    @GetMapping("/services")
    fun services(): ServiceConfiguration = configuration

    private fun String.throwIfSubPathIsEmpty(): String {
        if (this == "/services") {
            throw IllegalArgumentException("No compilation method specified.")
        } else {
            return this
        }
    }

    companion object {
        const val tempCompileFileName = "temp"
    }
}
