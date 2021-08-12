package com.tena.sbcommunity2021.articles.repository;

import com.tena.sbcommunity2021.articles.domain.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class ArticleRepository {

	// DB 내 데이터라 가정
	private static final List<Article> articles = new ArrayList<>();

	@PostConstruct
	private void initArticles() {
		IntStream.rangeClosed(1, 10)
				.mapToObj(i -> Article.builder().title("제목" + i).content("내용" + i).build())
				.forEach(articles::add);

		log.info("articles init size: {}", articles.size());
		log.info("articles : {}", articles);
	}


	public Article save(Article article) {
		articles.add(article);

		return article;
	}

	public List<Article> findAll() {
		return articles;
	}

	public Optional<Article> findById(Long id) {
		return articles.stream()
				.filter(article -> id.equals(article.getId()))
				.findFirst();
	}

	public void delete(Article article) {
		articles.remove(article);
	}

}
