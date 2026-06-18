package com.inventory.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Maps domain and validation failures to consistent HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles missing product lookups.
     *
     * @param exception not-found failure
     * @return 404 response
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("PRODUCT_NOT_FOUND", exception.getMessage(), null));
    }

    /**
     * Handles missing order lookups.
     *
     * @param exception not-found failure
     * @return 404 response
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderNotFound(OrderNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("ORDER_NOT_FOUND", exception.getMessage(), null));
    }

    /**
     * Handles checkout requests that exceed available stock.
     *
     * @param exception insufficient stock failure
     * @return 400 response
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(InsufficientStockException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("INSUFFICIENT_STOCK", exception.getMessage(), null));
    }

    /**
     * Handles checkout requests for inactive catalog products.
     *
     * @param exception inactive product failure
     * @return 400 response
     */
    @ExceptionHandler(ProductInactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleProductInactive(ProductInactiveException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("PRODUCT_INACTIVE", exception.getMessage(), null));
    }

    /**
     * Handles concurrent stock updates that fail optimistic locking.
     *
     * @param exception optimistic locking failure
     * @return 409 response
     */
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        "CONCURRENT_MODIFICATION",
                        "The product was modified by another request. Please retry.",
                        null));
    }

    /**
     * Handles request body validation failures.
     *
     * @param exception validation failure
     * @return 400 response with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<ApiErrorResponse.FieldError> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("VALIDATION_FAILED", "Request validation failed", fieldErrors));
    }
}
