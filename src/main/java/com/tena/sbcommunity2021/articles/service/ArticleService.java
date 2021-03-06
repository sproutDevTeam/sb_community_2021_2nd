package com.tena.sbcommunity2021.articles.service;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotCreatedException;
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

	public Article createArticle(Long accountId, ArticleDto.Save saveDto) {

		Article article = modelMapper.map(saveDto, Article.class); // 도메인 객체로 변환

		articleRepository.save(accountId, article); // 게시물 저장

		return articleRepository.findById(article.getId()).orElseThrow(ArticleNotCreatedException::new); // 저장한 게시물 조회
	}

	public Article updateArticle(Long id, ArticleDto.Save saveDto) {

		Article article = getArticle(id); // 기존 게시물 조회

		modelMapper.map(saveDto, article); // 기존값 변경

		articleRepository.update(article); // 게시물 업데이트

		return article;
	}

	public void deleteArticle(Long id) {

		Article article = getArticle(id);

		articleRepository.deleteById(id);
	}

}
