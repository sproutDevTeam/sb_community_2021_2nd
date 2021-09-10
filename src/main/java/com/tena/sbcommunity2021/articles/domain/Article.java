package com.tena.sbcommunity2021.articles.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
@Getter
@Setter
@Builder
public class Article {

	private Long id;

	private String title;

	private String content;

	private LocalDateTime regDate;

	private LocalDateTime updateDate;

	private Long accountId;

//	public void renewUpdateDate() {
//		this.updateDate = LocalDateTime.now();
//	}

}