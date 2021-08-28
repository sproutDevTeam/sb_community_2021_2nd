package com.tena.sbcommunity2021.articles.web;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

	private final ArticleService articleService;
	private final ModelMapper modelMapper;

	@GetMapping
	@ResponseBody
	public List<ArticleDto.Response> getArticles() {
		List<Article> articles = articleService.getArticles();
		
		// 👉 ModelMapper 에서 List 맵핑 시 Generic 변경 방법 2가지
		// 방법 1. 주로 Stream 을 돌리겠지만,
		// List<ArticleDto.Response> body = articles.stream().map(a -> modelMapper.map(a, ArticleDto.Response.class)).collect(Collectors.toList());
		// 방법 2. TypeToken 을 사용하는 방법도 있다.
		List<ArticleDto.Response> body = modelMapper.map(articles, new TypeToken<List<ArticleDto.Response>>() {}.getType());

		return body;
	}

	@GetMapping("/{id}")
	@ResponseBody
	public ArticleDto.Response getArticle(@PathVariable("id") Long id) {
		Article article = articleService.getArticle(id);

		return modelMapper.map(article, ArticleDto.Response.class);
	}

	@RequestMapping("/new")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public ArticleDto.Response createArticle(@Valid ArticleDto.Save saveDto) {
		Article article = articleService.createArticle(saveDto);

		return modelMapper.map(article, ArticleDto.Response.class);
	}

	@RequestMapping("/{id}/edit")
	@ResponseBody
	public ArticleDto.Response updateArticle(@PathVariable("id") Long id, @Valid ArticleDto.Save saveDto) {
		Article article = articleService.updateArticle(id, saveDto);

		return modelMapper.map(article, ArticleDto.Response.class);
	}

	@GetMapping("/{id}/delete")
	@ResponseBody
	public String deleteArticle(@PathVariable("id") Long id) {
		articleService.deleteArticle(id);

		return id + "번 게시물을 삭제하였습니다.";
	}

}
