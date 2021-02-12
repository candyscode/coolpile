package edu.hm.cs.coolpile.controller

import edu.hm.cs.coolpile.model.config.ServiceConfiguration
import edu.hm.cs.coolpile.model.compiler.CompilerServiceDescription
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
class CompileController(val serviceConfiguration: ServiceConfiguration) {

    @GetMapping("/compiler")
    fun getAllCompilers(): List<CompilerServiceDescription> {
        return serviceConfiguration.compilers.map {
            compiler -> CompilerServiceDescription(compiler.name, compiler.description)
        }
    }

    @GetMapping("/compiler/**")
    fun getCompiler(request: HttpServletRequest, @RequestBody compileRequest: String): String {
        val x = request

        return "x"
    }

    @PostMapping("/compiler")
    fun postCompiler(request: HttpServletRequest, @RequestBody compileRequest: String): String {
        println(compileRequest)
        return "x"
    }

    @DeleteMapping("/compiler/mixsaxri")
    fun deleteCompiler(request: HttpServletRequest, @RequestBody compileRequest: String): String {
        println(compileRequest)
        return "x"
    }

}
