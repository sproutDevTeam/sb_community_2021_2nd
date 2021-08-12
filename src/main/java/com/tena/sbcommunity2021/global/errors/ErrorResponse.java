package com.tena.sbcommunity2021.global.errors;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;
    private final int status;
    private final List<FieldError> errors;

    @Builder
    private ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus().value();
        this.errors = initErrors(errors);
    }

    private List<FieldError> initErrors(final List<FieldError> errors) {
        return (errors == null) ? new ArrayList<>() : errors;
    }

    public static ErrorResponse buildError(final ErrorCode errorCode) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .build();
    }

    public static ErrorResponse buildFieldErrors(final ErrorCode errorCode, final List<FieldError> errors) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errors(errors)
                .build();
    }

    public static ErrorResponse buildFieldErrors(final ErrorCode errorCode, final BindingResult bindingResult) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errors(FieldError.getFieldErrors(bindingResult))
                .build();
    }

    public static ErrorResponse buildFieldErrors(final ErrorCode errorCode, final Set<ConstraintViolation<?>> constraintViolations) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errors(FieldError.getFieldErrors(constraintViolations))
                .build();
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