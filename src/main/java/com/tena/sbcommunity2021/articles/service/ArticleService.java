package com.tena.sbcommunity2021.articles.service;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;

	public Article createArticle(ArticleDto.Save saveDto) {
		return articleRepository.save(saveDto.toDomain());
	}

	public List<Article> getArticles() {
		return articleRepository.findAll();
	}

	public Article getArticle(Long id) {
		return articleRepository.findById(id).orElseThrow(() -> new ArticleNotFoundException(id));
	}

	public void deleteArticle(Long id) {
		Article article = getArticle(id);

		articleRepository.delete(article);
	}

	public Article updateArticle(Long id, ArticleDto.Save saveDto) {
		Article article = getArticle(id);

		article.updateArticle(saveDto);

		return article;
	}

}
