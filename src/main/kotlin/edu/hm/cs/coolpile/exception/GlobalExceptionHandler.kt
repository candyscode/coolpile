package edu.hm.cs.coolpile.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*


@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(GCCException::class)
    fun generateGCCException(ex: GCCException): ResponseEntity<ErrorDTO> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val errorDTO = ErrorDTO(
                status = httpStatus.name,
                errorType = "Compilation Error",
                message = ex.message,
                time = Date().toString()
        )
        return ResponseEntity<ErrorDTO>(errorDTO, httpStatus)
    }
}