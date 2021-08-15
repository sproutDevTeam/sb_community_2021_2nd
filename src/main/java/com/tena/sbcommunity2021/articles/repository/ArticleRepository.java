package com.tena.sbcommunity2021.articles.repository;

import com.tena.sbcommunity2021.articles.domain.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ArticleRepository {

	// DB 내 데이터라 가정
	private List<Article> articles;

	@PostConstruct
	private void initArticles() {
		articles = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {
			Article article = new Article("제목" + i, "내용" + i);
			articles.add(article);
		}

		log.info("articles init size: {}", articles.size());
		log.info("articles : {}", articles);
	}


	public Article save(Article article) {
		articles.add(article);

		return article;
	}

	public List<Article> findAll() {
		return articles == null ? List.of() : articles;
	}

	public Optional<Article> findById(Long id) {
		return articles.stream()
				.filter(article -> id.equals(article.getId()))
				.findFirst();
	}

	public void delete(Article article) {
		articles.remove(article);
	}

	public void deleteAll() {
		articles.clear();
	}

}
