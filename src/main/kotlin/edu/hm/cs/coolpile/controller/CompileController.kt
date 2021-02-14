package edu.hm.cs.coolpile.controller

import edu.hm.cs.coolpile.exception.CompilerNotFoundException
import edu.hm.cs.coolpile.model.compiler.CompilerService
import edu.hm.cs.coolpile.model.config.ServiceConfiguration
import edu.hm.cs.coolpile.model.compiler.CompilerServiceDescription
import edu.hm.cs.coolpile.model.dto.CompileRequest
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
class CompileController(val serviceConfiguration: ServiceConfiguration) {

    @GetMapping("/compiler")
    fun getAllCompilers() = serviceConfiguration.compilers.map {
        compiler -> CompilerServiceDescription(compiler.name, compiler.description)
    }

    @GetMapping("/compiler/**")
    fun getCompiler(request: HttpServletRequest, @RequestBody compileRequest: CompileRequest) =
            request.getMatchingCompiler().compile(
                    sourceCode = compileRequest.sourceCode,
                    sessionId = request.session.id
            )

    @PostMapping("/compiler")
    fun postCompiler(@RequestBody compiler: CompilerService) =
            serviceConfiguration.addCompiler(compiler)

    @DeleteMapping("/compiler/**")
    fun deleteCompiler(request: HttpServletRequest) =
            serviceConfiguration.removeCompiler(request.getMatchingCompiler().name)

    private fun HttpServletRequest.getMatchingCompiler(): CompilerService {
        val compilerName = requestURI.removePrefix("/compiler/")

        if (compilerName.length >= requestURI.length || compilerName.contains("/")) {
            throw CompilerNotFoundException("Compiler $compilerName not found.")
        }

        return serviceConfiguration.compilers.firstOrNull { compilerService ->
            compilerService.name == compilerName
        } ?: throw CompilerNotFoundException("Compiler $compilerName not found.")
    }
}
