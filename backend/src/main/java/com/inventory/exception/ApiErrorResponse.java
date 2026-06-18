package com.inventory.exception;

import java.util.List;

/**
 * Consistent error payload returned by the API.
 *
 * @param code machine-readable error code
 * @param message human-readable summary
 * @param fieldErrors optional field-level validation errors
 */
public record ApiErrorResponse(
        String code,
        String message,
        List<FieldError> fieldErrors) {

    /**
     * A single field validation failure.
     *
     * @param field request field name
     * @param message validation message
     */
    public record FieldError(String field, String message) {
    }
}
