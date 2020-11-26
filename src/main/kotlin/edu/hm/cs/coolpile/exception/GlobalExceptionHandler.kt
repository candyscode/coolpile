package edu.hm.cs.coolpile.exception

import edu.hm.cs.coolpile.dto.Error
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*


@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(GCCException::class)
    fun generateGCCException(ex: GCCException): ResponseEntity<Error> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val errorDTO = Error(
                status = httpStatus.name,
                errorType = "Compilation Error",
                message = ex.message,
                time = Date().toString()
        )
        return ResponseEntity<Error>(errorDTO, httpStatus)
    }
}