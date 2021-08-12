package com.tena.sbcommunity2021.articles.exception;

import com.tena.sbcommunity2021.global.errors.ErrorCode;
import com.tena.sbcommunity2021.global.errors.exception.CustomException;

public class ArticleNotFoundException extends CustomException {

	public ArticleNotFoundException() {
		super(ErrorCode.ARTICLE_NOT_FOUND);
	}

}
