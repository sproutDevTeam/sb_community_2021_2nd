package com.tena.sbcommunity2021.articles.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
@ToString
@Getter
public class Article {

	private Long id;

	private String title;

	private String content;

	private LocalDateTime regDate;

	private LocalDateTime updateDate;

	public void renewUpdateDate() {
		this.updateDate = LocalDateTime.now();
	}

}