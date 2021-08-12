package com.tena.sbcommunity2021.articles.web;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

	private final ArticleService articleService;

	@RequestMapping
	@ResponseBody
	public List<ArticleDto.Response> getArticles() {
		List<Article> articles = articleService.getArticles();
		
		return articles.stream()
				.map(ArticleDto.Response::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	@ResponseBody
	public ArticleDto.Response getArticle(@PathVariable("id") Long id) {
		Article article = articleService.getArticle(id);

		return new ArticleDto.Response(article);
	}

	@RequestMapping("/new")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public ArticleDto.Response createArticle(@Valid ArticleDto.Save saveDto) {
		Article article = articleService.createArticle(saveDto);

		return new ArticleDto.Response(article);
	}

	@RequestMapping("/{id}/edit")
	@ResponseBody
	public ArticleDto.Response updateArticle(@PathVariable("id") Long id, @Valid ArticleDto.Save saveDto) {
		Article article = articleService.updateArticle(id, saveDto);

		return new ArticleDto.Response(article);
	}

	@GetMapping("/{id}/delete")
	@ResponseBody
	public String deleteArticle(@PathVariable("id") Long id) {
		articleService.deleteArticle(id);

		return id + "번 게시물을 삭제하였습니다.";
	}

}
