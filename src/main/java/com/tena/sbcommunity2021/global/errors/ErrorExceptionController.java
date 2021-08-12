package com.tena.sbcommunity2021.global.errors;

import com.tena.sbcommunity2021.global.errors.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static com.tena.sbcommunity2021.global.errors.ErrorResponse.*;

@Slf4j
@ControllerAdvice
@ResponseBody
public class ErrorExceptionController {

	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ErrorResponse handleCustomException(final CustomException e) {
		log.error("handleCustomException", e);
		log.error("errorCode : {}", e.getErrorCode());
		return buildError(e.getErrorCode());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
		log.error("handleConstraintViolationException", e);
		return buildFieldErrors(ErrorCode.INVALID_INPUT_VALUE, e.getConstraintViolations());
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleBindException(final BindException e) {
		log.error("handleBindException", e);
		return buildFieldErrors(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		log.error("handleMethodArgumentNotValidException", e);
		return buildFieldErrors(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
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
		final List<FieldError> fieldErrors = FieldError.getFieldErrors(field, value, reason);

		return buildFieldErrors(ErrorCode.INVALID_TYPE_VALUE, fieldErrors);
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

}