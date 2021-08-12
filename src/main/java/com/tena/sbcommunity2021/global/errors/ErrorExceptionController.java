package com.tena.sbcommunity2021.global.errors;

import com.tena.sbcommunity2021.global.errors.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@ResponseBody
public class ErrorExceptionController {

	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ErrorResponse handleCustomException(final CustomException e) {
		log.error("handleCustomException", e);
		log.error("errorCode : {}", e.getErrorCode());
		final ErrorCode errorCode = e.getErrorCode();

		return buildError(errorCode);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
		log.error("handleConstraintViolationException", e);
		final List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getConstraintViolations());

		return buildFieldErrors(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleBindException(final BindException e) {
		log.error("handleBindException", e);
		final List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());

		return buildFieldErrors(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		log.error("handleMethodArgumentNotValidException", e);
		final List<ErrorResponse.FieldError> fieldErrors = getFieldErrors(e.getBindingResult());

		return buildFieldErrors(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
		log.error("handleMethodArgumentTypeMismatchException", e);

		final String field = e.getName();
		final String value = (String) e.getValue();
		final String requiredType = e.getRequiredType().getSimpleName();
		final String reason = field + " should be of type " + requiredType + ". Current input value: {" + value + "}";
		// final String reason = e.getErrorCode();
		final ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError(field, value, reason);

		return buildFieldErrors(ErrorCode.INVALID_TYPE_VALUE, List.of(fieldError));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	protected ErrorResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
		log.error("handleHttpRequestMethodNotSupportedException", e);

		return buildError(ErrorCode.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ErrorResponse handleException(final Exception e) {
		log.error("handleException", e);
		return buildError(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private List<ErrorResponse.FieldError> getFieldErrors(final BindingResult bindingResult) {
		final List<FieldError> errors = bindingResult.getFieldErrors();
		return errors.parallelStream()
				.map(error -> ErrorResponse.FieldError.builder()
						.reason(error.getDefaultMessage())
						.field(error.getField())
						.value((String) error.getRejectedValue())
						.build())
				.collect(Collectors.toList());
	}

	private List<ErrorResponse.FieldError> getFieldErrors(final Set<ConstraintViolation<?>> constraintViolations) {
		return constraintViolations.parallelStream()
				.map(error -> ErrorResponse.FieldError.builder()
						.reason(error.getMessage())
						.value(error.getInvalidValue().toString())
						.field(error.getPropertyPath().toString())
						.build())
				.collect(Collectors.toList());
	}

	private ErrorResponse buildError(final ErrorCode errorCode) {
		return ErrorResponse.builder()
				.code(errorCode.getCode())
				.status(errorCode.getStatus().value())
				.message(errorCode.getMessage())
				.build();
	}

	private ErrorResponse buildFieldErrors(final ErrorCode errorCode, final List<ErrorResponse.FieldError> errors) {
		return ErrorResponse.builder()
				.code(errorCode.getCode())
				.status(errorCode.getStatus().value())
				.message(errorCode.getMessage())
				.errors(errors)
				.build();
	}

}