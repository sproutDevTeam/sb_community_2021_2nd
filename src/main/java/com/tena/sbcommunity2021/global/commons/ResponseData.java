package com.tena.sbcommunity2021.global.commons;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ResponseData<T> {

	private String resultCode;

	private String message;

	private T body;

	public boolean isSuccess() {
		return resultCode.startsWith("S-");
	}

	public boolean isFail() {
		return !isSuccess();
	}

	@Builder
	private ResponseData(String resultCode, String message, T body) {
		this.resultCode = resultCode;
		this.message = message;
		this.body = body;
	}

	public static <T> ResponseData<T> of(String resultCode, String message, T body) {
		return ResponseData.<T>builder().resultCode(resultCode).message(message).body(body).build(); // return new ResultData<>(resultCode, message, body);
	}

	public static <T> ResponseData<T> of(String resultCode, String message) {
		return ResponseData.<T>builder().resultCode(resultCode).message(message).build(); // return of(resultCode, message, null);
	}

	public static <T> ResponseData<T> of(String resultCode, T body) {
		return ResponseData.<T>builder().resultCode(resultCode).body(body).build(); // return of(resultCode, null, body);
	}

	public static <T> ResponseData<T> of(ResponseData<T> join, T body) {
		return ResponseData.<T>builder().resultCode(join.getResultCode()).message(join.getMessage()).body(body).build(); // return of(joinRd.getResultCode(), joinRd.getMessage(), body);
	}

}
