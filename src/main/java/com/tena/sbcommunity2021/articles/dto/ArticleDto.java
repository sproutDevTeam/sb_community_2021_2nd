package com.tena.sbcommunity2021.articles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tena.sbcommunity2021.articles.domain.Article;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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

		// TODO ModelMapper 로 대체할 것
		// Save DTO to Domain Object
		public Article toDomain() {
			return Article.builder()
					.title(this.title)
					.content(this.content)
					.build();
		}

	}

	@JsonIgnoreProperties(value = {"formatterToRemoveNanoSec"})
	@ToString
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Response {
		private Long id;
		private String title;
		private String content;
		private String regDate;
		private String updateDate;

		// TODO ModelMapper 로 대체는 VIEW 도입 시점에 고려
		// Domain Object to Response DTO
		public Response(Article article) {
			this.id = article.getId();
			this.title = article.getTitle();
			this.content = article.getContent();
			this.regDate = formatterToRemoveNanoSec.apply(article.getRegDate());
			this.updateDate = formatterToRemoveNanoSec.apply(article.getUpdateDate());
		}

        // TODO @JsonFormat 사용 고려
		protected Function<LocalDateTime, String> formatterToRemoveNanoSec =
				localDateTime -> localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
	}

}