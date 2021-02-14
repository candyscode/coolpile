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
    fun generateIllegalArgumentException(exception: IllegalArgumentException): ResponseEntity<Error> =
            exception.generate("Illegal Arguments", HttpStatus.BAD_REQUEST)

    @ExceptionHandler(InvalidEndpointException::class)
    fun generateInvalidEndpointException(exception: InvalidEndpointException): ResponseEntity<Error> =
            exception.generate("Invalid Endpoint", HttpStatus.NOT_FOUND)

    private fun Exception.generate(errorType: String, httpStatus: HttpStatus): ResponseEntity<Error> {
        val errorDTO = Error(
                status = httpStatus.value().toString(),
                error = errorType,
                message = message ?: "",
                timestamp = Date().toString()
        )
        return ResponseEntity<Error>(errorDTO, httpStatus)
    }
}