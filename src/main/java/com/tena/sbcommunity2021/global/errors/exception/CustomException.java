package com.tena.sbcommunity2021.global.errors.exception;

import com.tena.sbcommunity2021.global.errors.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	protected CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

}