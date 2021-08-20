package com.tena.sbcommunity2021.articles.repository;

import com.tena.sbcommunity2021.articles.domain.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArticleRepository {

	List<Article> findAll();

	Optional<Article> findById(Long id);

	void save(Article article);

	void update(Article article);

	void deleteById(Long id);

	void deleteAll();

	boolean existsById(Long id);

}