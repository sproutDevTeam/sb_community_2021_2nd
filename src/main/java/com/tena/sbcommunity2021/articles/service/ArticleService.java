package com.tena.sbcommunity2021.articles.service;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final ModelMapper modelMapper;

	@Transactional(readOnly = true)
	public List<Article> getArticles() {
		return articleRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Article getArticle(Long id) {
		return articleRepository.findById(id).orElseThrow(ArticleNotFoundException::new);
	}

	public Article createArticle(ArticleDto.Save saveDto) {
		Article article = modelMapper.map(saveDto, Article.class); // 도메인 객체로 변환

		articleRepository.save(article); // 게시물 저장

		return getArticle(article.getId()); // 저장한 게시물 조회
	}

	public Article updateArticle(Long id, ArticleDto.Save saveDto) {
		Article article = getArticle(id);

		modelMapper.map(saveDto, article);
		article.renewUpdateDate();

		articleRepository.update(article);

		return article;
	}

	public void deleteArticle(Long id) {
		Article article = getArticle(id);

		articleRepository.deleteById(article.getId());
	}

}
