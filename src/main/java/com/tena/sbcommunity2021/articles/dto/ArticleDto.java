package com.tena.sbcommunity2021.articles.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ArticleDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Save {

		@NotBlank(message = "제목을 입력해주세요.")
		private String title;

		@NotBlank(message = "내용을 입력해주세요.")
		private String content;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Response {

		private Long id;

		private String title;

		private String content;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime regDate;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime updateDate;
	}

}