package com.tena.sbcommunity2021.articles.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArticleNotFoundException extends RuntimeException {

	private final Long id;

}
