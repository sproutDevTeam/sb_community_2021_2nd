package com.tena.sbcommunity2021.global.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	/* Common */
	// 400 BAD_REQUEST : 잘못된 요청
	INVALID_INPUT_VALUE("C-400", "잘못된 입력 값이 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
	INVALID_TYPE_VALUE("C-400", "잘못된 유형의 값이 포함되어 있습니다.", HttpStatus.BAD_REQUEST),

	// 405 METHOD_NOT_ALLOWED : 허용되지 않은 Request Method 호출
	METHOD_NOT_ALLOWED("C-405", "허용되지 않은 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),

	// 500 INTERNAL_SERVER_ERROR : 서버 에러
	INTERNAL_SERVER_ERROR("C-500", "내부 서버 오류입니다. 관리자에게 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR),

	/* Article */
	ARTICLE_NOT_FOUND("A-001", "게시물이 존재하지 않습니다.", HttpStatus.NOT_FOUND)

	;

	private final String code;
	private final String message;
	private final HttpStatus status;

}