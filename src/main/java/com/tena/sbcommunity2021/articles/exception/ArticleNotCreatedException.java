package com.tena.sbcommunity2021.articles.exception;

import com.tena.sbcommunity2021.global.errors.ErrorCode;
import com.tena.sbcommunity2021.global.errors.exception.CustomException;

public class ArticleNotCreatedException extends CustomException {

	public ArticleNotCreatedException() {
		super(ErrorCode.ARTICLE_NOT_CREATED);
	}

}
