package com.tena.sbcommunity2021.articles.dto;

import com.tena.sbcommunity2021.articles.domain.Article;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class ArticleDto {

	@ToString
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Save {

		@NotBlank
		private String title;

		@NotBlank
		private String content;

		@Builder
		public Save(String title, String content) {
			this.title = title;
			this.content = content;
		}

		// Save DTO to Domain Object
		public Article toDomain() {
			return Article.builder()
					.title(this.title)
					.content(this.content)
					.build();
		}

	}

	@ToString
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Response {
		private Long id;
		private String title;
		private String content;

		// Domain Object to Response DTO
		public Response(Article article) {
			this.id = article.getId();
			this.title = article.getTitle();
			this.content = article.getContent();
		}
	}

}
