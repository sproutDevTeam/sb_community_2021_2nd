package com.tena.sbcommunity2021.articles.web;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import com.tena.sbcommunity2021.test.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
class ArticleControllerTest extends IntegrationTest {

	@Autowired ArticleRepository articleRepository;
	@Autowired ArticleService articleService;

	@BeforeEach
	void setUp() {
		this.articleRepository.deleteAll();
	}

	private ArticleDto.Save buildDtoToCreate() {
		return ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();
	}

	private Article createArticle() {
		final ArticleDto.Save dto = buildDtoToCreate();
		return this.articleService.createArticle(dto);
	}

	@Test
	@DisplayName("신규 게시물 작성 요청/응답 - 201, 정상적으로 작성")
	void createArticle_success() throws Exception {
		//given
		final ArticleDto.Save saveDto = buildDtoToCreate();

		//when
		final ResultActions resultActions = requestCreateArticle(saveDto);

		//then
		resultActions
				.andExpect(status().isCreated()) // 201
				.andExpect(jsonPath("title").value(saveDto.getTitle()))
				.andExpect(jsonPath("content").value(saveDto.getContent()));
	}

	@Test
	@DisplayName("신규 게시물 작성 요청/응답 - 400, 입력값이 잘못된 경우 업데이트 실패 (유효성 검사 실패)")
	void createArticle_validation_fail() throws Exception {
		//given
		final ArticleDto.Save saveDto = ArticleDto.Save.builder().title("   ").content("내용").build(); // blank (white space only)
		// final ArticleDto.Save saveDto = ArticleDto.Save.builder().title("").content("내용").build(); // empty
		// final ArticleDto.Save saveDto = ArticleDto.Save.builder().title(null).content("내용").build(); // null

		//when
		final ResultActions resultActions = requestCreateArticle(saveDto);

		//then
		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").exists())
				.andExpect(jsonPath("errors.length()").value(1))

		// for increasing coverage
				.andExpect(jsonPath("errors[0].field").value("title"))
				.andExpect(jsonPath("errors[0].value").value(saveDto.getTitle()))
				.andExpect(jsonPath("errors[0].reason").isNotEmpty());
	}

	private ResultActions requestCreateArticle(ArticleDto.Save saveDto) throws Exception {
		return mockMvc.perform(postForm("/articles/new", saveDto)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}
	

	@Test
	@DisplayName("특정 게시물 하나 조회 요청/응답 - 200, 정상적으로 조회")
	void getArticle_success() throws Exception {
		//given
		final Article article = this.createArticle();

		//when
		final ResultActions resultActions = requestGetArticle(article.getId());

		//then
		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("title").value(article.getTitle()))
				.andExpect(jsonPath("content").value(article.getContent()))
				.andDo(print());
	}

	@Test
	@DisplayName("특정 게시물 하나 조회 요청/응답 - 404, 해당 게시물이 존재하지 않는 경우 (예외 발생)")
	void getArticle_404() throws Exception {
		//given

		//when
		final ResultActions resultActions = requestGetArticle(Long.MAX_VALUE);

		//then
		resultActions
				.andExpect(status().isNotFound()) // 404
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty())
				.andDo(print());
	}

	private ResultActions requestGetArticle(Long id) throws Exception {
		return mockMvc.perform(get("/articles/{id}", id)
						.accept(MediaType.APPLICATION_JSON_UTF8));
	}


	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200, 정상적으로 조회")
	void getArticles_success() throws Exception {
		//given
		final Article article = this.createArticle();
		final Article article2 = this.createArticle();

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0]").exists())
				.andExpect(jsonPath("$[0].id").value(article.getId()))
				.andExpect(jsonPath("$[0].title").value(article.getTitle()))
				.andExpect(jsonPath("$[0].content").value(article.getContent()))
				.andExpect(jsonPath("$[1]").exists())
				.andExpect(jsonPath("$[1].id").value(article2.getId()))
				.andExpect(jsonPath("$[1].title").value(article2.getTitle()))
				.andExpect(jsonPath("$[1].content").value(article2.getContent()));
	}

	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200, 게시물이 하나도 존재하지 않을 경우")
	void getArticles_nothingAtAll() throws Exception {
		//given

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	private ResultActions requestGetAllArticles() throws Exception {
		return mockMvc.perform(get("/articles")
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}


	@Test
	@DisplayName("기존 게시물 업데이트 요청/응답 - 200, 정상적으로 수정")
	void updateArticle_success() throws Exception {
		//given
		final Article article = this.createArticle();

		final ArticleDto.Save updateDto = modelMapper.map(article, ArticleDto.Save.class);
		updateDto.setTitle("제목수정");

		//when
		final ResultActions resultActions =
				mockMvc.perform(post("/articles/{id}/edit", article.getId())
						.param("title", updateDto.getTitle())
						.param("content", updateDto.getContent())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON_UTF8));

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("title").value(updateDto.getTitle()))
				.andDo(print());
	}

	@Test
	@DisplayName("기존 게시물 업데이트 요청/응답 - 400, 입력값이 누락/잘못된 경우 업데이트 실패 (유효성 검사 실패)")
	void updateArticle_validation_fail() throws Exception {
		//given
		final Article article = this.createArticle();

		final ArticleDto.Save updateDto = modelMapper.map(article, ArticleDto.Save.class);
		updateDto.setTitle("  "); // blank (white space only)
		updateDto.setContent(null); // null

		//when
		final ResultActions resultActions =
				mockMvc.perform(post("/articles/{id}/edit", article.getId())
								.param("title", updateDto.getTitle())
								.param("content", updateDto.getContent())
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.accept(MediaType.APPLICATION_JSON_UTF8))
						.andDo(print());
		//then
		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").exists())
				.andExpect(jsonPath("errors.length()").value(2));
	}

	@Test
	@DisplayName("기존 게시물 삭제 요청/응답 - 200, 정상적으로 삭제")
	void deleteArticle() throws Exception {
		//given
		final Article article = this.createArticle();

		//when
		final ResultActions resultActions =
				mockMvc.perform(get("/articles/{id}/delete", article.getId())
								.accept(MediaType.APPLICATION_JSON_UTF8))
						.andDo(print());

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("번 게시물을 삭제하였습니다.")));
	}

	@Test
	@DisplayName("기존 게시물 삭제 요청/응답 - 404, 존재하지 않는 게시물 삭제 실패 (예외 발생)")
	void deleteArticle_404() throws Exception {
		//given

		//when
		final ResultActions resultActions =
				mockMvc.perform(get("/articles/{id}/delete", Long.MAX_VALUE)
								.accept(MediaType.APPLICATION_JSON_UTF8))
						.andDo(print());

		//then
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());
	}

}