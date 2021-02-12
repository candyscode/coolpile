package edu.hm.cs.coolpile.exception

import edu.hm.cs.coolpile.model.dto.Error
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.lang.IllegalArgumentException
import java.util.*

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun generateIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Error> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val errorDTO = Error(
                status = httpStatus.name,
                errorType = "Illegal Arguments",
                message = ex.message ?: "",
                time = Date().toString()
        )
        return ResponseEntity<Error>(errorDTO, httpStatus)
    }
}