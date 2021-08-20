package com.tena.sbcommunity2021.test.setup;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class ArticleSetup {

	@Autowired ArticleRepository articleRepository;
	@Autowired ArticleService articleService;

	public Article createArticle() {
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();

		return articleService.createArticle(dto);
	}

	public void deleteAllArticles() {
		articleRepository.deleteAll();
	}

}