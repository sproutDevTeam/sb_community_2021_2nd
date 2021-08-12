package com.tena.sbcommunity2021.articles.domain;

import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@ToString
@Getter
public class Article {

	private static Long LAST_INSERT_ID = 0L; // DB 연동 전, 테스트를 위해 사용하는 임시 변수

	private Long id;

	private String title;

	private String content;

	@Builder
	public Article(String title, String content) {
		this.id = ++LAST_INSERT_ID;

		this.title = title;
		this.content = content;
	}

	public void updateArticle(ArticleDto.Save saveDto) {
		this.title = saveDto.getTitle();
		this.content = saveDto.getContent();
	}

}