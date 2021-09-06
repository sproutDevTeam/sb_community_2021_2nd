package com.tena.sbcommunity2021.articles.web;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.commons.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

	private final ArticleService articleService;
	private final ModelMapper modelMapper;

	@GetMapping
	@ResponseBody
	public ResponseData<List<ArticleDto.Response>> getArticles() {
		List<Article> articles = articleService.getArticles();

		List<ArticleDto.Response> body = articles.stream()
				.map(article -> modelMapper.map(article, ArticleDto.Response.class))
				.collect(Collectors.toList());
		// List<ArticleDto.Response> body = modelMapper.map(articles, new TypeToken<List<ArticleDto.Response>>() {}.getType());

		return ResponseData.of("S-1", "게시물 목록입니다.", body);
	}

	@GetMapping("/{id}")
	@ResponseBody
	public ResponseData<ArticleDto.Response> getArticle(@PathVariable("id") Long id) {
		Article article = articleService.getArticle(id);

		ArticleDto.Response body = modelMapper.map(article, ArticleDto.Response.class);

		return ResponseData.of("S-1", String.format("%d번 게시물입니다.", id), body);
	}

	@RequestMapping("/new")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseData<ArticleDto.Response> createArticle(@Valid ArticleDto.Save saveDto) {
		Article article = articleService.createArticle(saveDto);

		ArticleDto.Response body = modelMapper.map(article, ArticleDto.Response.class);

		return ResponseData.of("S-1", "게시물이 작성되었습니다.", body);
	}

	@RequestMapping("/{id}/edit")
	@ResponseBody
	public ResponseData<ArticleDto.Response> updateArticle(@PathVariable("id") Long id, @Valid ArticleDto.Save saveDto) {
		Article article = articleService.updateArticle(id, saveDto);

		ArticleDto.Response body = modelMapper.map(article, ArticleDto.Response.class);

		return ResponseData.of("S-1", String.format("%d번 게시물을 수정하였습니다.", id), body);
	}

	@GetMapping("/{id}/delete")
	@ResponseBody
	public ResponseData<Object> deleteArticle(@PathVariable("id") Long id) {
		articleService.deleteArticle(id);

		return ResponseData.of("S-1", String.format("%d번 게시물을 삭제하였습니다.", id));
	}

}
