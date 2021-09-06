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

// [기존] 수정일자 갱신
//	public void renewUpdateDate() {
//		this.updateDate = LocalDateTime.now();
//	}

}