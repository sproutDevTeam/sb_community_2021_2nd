package com.tena.sbcommunity2021.global.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	/* Common */
	// 400 Bad Request
	INVALID_INPUT_VALUE("C-400", "잘못된 입력 값이 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
	INVALID_TYPE_VALUE("C-400", "잘못된 유형의 값이 포함되어 있습니다.", HttpStatus.BAD_REQUEST),

	// 401 Unauthorized
	UNAUTHORIZED("C-401", "로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED),

	// 403 Forbidden
	FORBIDDEN("C-403", "권한이 없습니다.", HttpStatus.FORBIDDEN),

	// 405 Method Not Allowed : 허용되지 않은 Request Method 호출
	METHOD_NOT_ALLOWED("C-405", "허용되지 않은 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),

	// 500 Internal Server Error : 서버 에러
	INTERNAL_SERVER_ERROR("C-500", "내부 서버 오류입니다. 관리자에게 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR),

	/* Article */
	ARTICLE_NOT_FOUND("A-001", "게시물이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	ARTICLE_NOT_CREATED("A-002", "게시물 작성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;

}