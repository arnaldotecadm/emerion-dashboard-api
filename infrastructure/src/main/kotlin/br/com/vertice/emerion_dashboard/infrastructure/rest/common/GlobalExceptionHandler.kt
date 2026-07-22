package br.com.vertice.emerion_dashboard.infrastructure.rest.common

import br.com.vertice.emerion_dashboard.domain.customer.exception.CustomerNotFoundException
import br.com.vertice.emerion_dashboard.domain.customeraddress.exception.CustomerAddressNotFoundException
import br.com.vertice.emerion_dashboard.domain.customercredit.exception.CustomerCreditNotFoundException
import br.com.vertice.emerion_dashboard.domain.customerorder.exception.CustomerOrderNotFoundException
import br.com.vertice.emerion_dashboard.domain.product.exception.ProductNotFoundException
import br.com.vertice.emerion_dashboard.domain.vendedor.exception.VendedorNotFoundException
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ErrorResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ErrorResponseError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.OffsetDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(
        CustomerNotFoundException::class,
        ProductNotFoundException::class,
        CustomerAddressNotFoundException::class,
        CustomerCreditNotFoundException::class,
        CustomerOrderNotFoundException::class,
        VendedorNotFoundException::class,
    )
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody("RESOURCE_NOT_FOUND", ex.message))

    /**
     * Any request path that doesn't match a controller or a static resource
     * (e.g. a typo'd endpoint) lands here as `NoResourceFoundException`
     * (Spring MVC's resource-handler fallback) rather than as a business
     * exception. Without this handler it would otherwise fall through to
     * the generic `Exception` handler below and be misreported as a 500.
     */
    @ExceptionHandler(NoResourceFoundException::class, NoHandlerFoundException::class)
    fun handleRouteNotFound(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody("ROUTE_NOT_FOUND", "No endpoint matches this request"))

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
