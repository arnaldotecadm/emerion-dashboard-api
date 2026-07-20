package br.com.vertice.emerion_dashboard.infrastructure.rest.common

import br.com.vertice.emerion_dashboard.domain.customer.CustomerNotFoundException
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ErrorResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ErrorResponseError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.OffsetDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleNotFound(ex: CustomerNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody("RESOURCE_NOT_FOUND", ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(errorBody("VALIDATION_ERROR", "Validation failed", details))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorBody("INTERNAL_SERVER_ERROR", "An unexpected error occurred"))
    }

    private fun errorBody(code: String, message: String?, details: String? = null) =
        ErrorResponse(
            error = ErrorResponseError(code = code, message = message ?: "Unexpected error", details = details),
            timestamp = OffsetDateTime.now(),
        )
}
