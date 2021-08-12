package com.tena.sbcommunity2021.global.errors;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {

    private final String timestamp;
    private final String message;
    private final String code;
    private final int status;
    private final String error;
    private final List<FieldError> errors;

    @Builder
    private ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.errors = initErrors(errors);
    }

    private List<FieldError> initErrors(final List<FieldError> errors) {
        return (errors == null) ? new ArrayList<>() : errors;
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(final ErrorCode errorCode) {
        final ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode).build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(final ErrorCode errorCode, final List<FieldError> errors) {
        final ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode)
                .errors(errors).build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(final ErrorCode errorCode, final BindingResult bindingResult) {
        final ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode)
                .errors(FieldError.getFieldErrors(bindingResult)).build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(final ErrorCode errorCode, final Set<ConstraintViolation<?>> constraintViolations) {
        final ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode)
                .errors(FieldError.getFieldErrors(constraintViolations)).build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @Getter
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        @Builder
        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> getFieldErrors(final String field, final String value, final String reason) {
            return List.of(new FieldError(field, value, reason));
        }

        private static List<FieldError> getFieldErrors(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> errors = bindingResult.getFieldErrors();
            return errors.parallelStream()
                    .map(error -> FieldError.builder()
                            .field(error.getField())
                            .value(String.valueOf(error.getRejectedValue()))
                            .reason(error.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
        }

        private static List<FieldError> getFieldErrors(final Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.parallelStream()
                    .map(error -> FieldError.builder()
                            .field(String.valueOf(error.getPropertyPath()))
                            .value(String.valueOf(error.getInvalidValue()))
                            .reason(error.getMessage())
                            .build())
                    .collect(Collectors.toList());
        }

    }

}