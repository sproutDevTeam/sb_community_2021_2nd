package com.tena.sbcommunity2021.articles.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class ArticleDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Save {

		@NotBlank
		private String title;

		@NotBlank
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

		// TODO : LocalDateTime + @JsonFormat 사용 고려
		private String regDate;
		private String updateDate;
	}

}