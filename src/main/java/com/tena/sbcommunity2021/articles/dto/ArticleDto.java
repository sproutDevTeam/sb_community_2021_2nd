package com.tena.sbcommunity2021.articles.dto;

import com.tena.sbcommunity2021.articles.domain.Article;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ArticleDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Save {
		private String title;
		private String content;

		@Builder
		public Save(String title, String content) {
			this.title = title;
			this.content = content;
		}

		public Article toDomain() {
			return Article.builder()
					.title(this.title)
					.content(this.content)
					.build();
		}
	}

	@Getter
	public static class Response {
		private Long id;
		private String title;
		private String content;

		public Response(Article article) {
			this.id = article.getId();
			this.title = article.getTitle();
			this.content = article.getContent();
		}
	}

}
