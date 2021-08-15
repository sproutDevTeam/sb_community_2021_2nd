package com.tena.sbcommunity2021.articles.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(ArticleController.class)
class ArticleControllerMockTestV2 {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ArticleService articleService;

	private ArticleDto.Save buildDtoToCreate() {
		return ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();
	}

	private ArticleDto.Save buildDtoToUpdate() {
		return ArticleDto.Save.builder()
				.title("제목수정")
				.content("내용수정")
				.build();
	}


	@Test
	@DisplayName("게시물 작성 요청/응답 - 201")
	void createArticle_success() throws Exception {
		//given
		final ArticleDto.Save saveDto = buildDtoToCreate();

		Mockito.when(articleService.createArticle(any())).thenReturn(saveDto.toDomain());

		//when
		final ResultActions resultActions = requestCreateArticle(saveDto);

		//then
		resultActions
				.andExpect(status().isCreated()) // 201
				.andExpect(jsonPath("title").value(saveDto.getTitle()))
				.andExpect(jsonPath("content").value(saveDto.getContent()));
	}

	@Test
	@DisplayName("게시물 작성 요청/응답 - 400, 입력값이 잘못된 경우 유효성 검사 실패")
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
//				.andExpect(jsonPath("message", is(ErrorCode.INVALID_INPUT_VALUE.getMessage())))
//				.andExpect(jsonPath("code", is(ErrorCode.INVALID_INPUT_VALUE.getCode())))
//				.andExpect(jsonPath("status", is(ErrorCode.INVALID_INPUT_VALUE.getStatus().value())))
				.andExpect(jsonPath("errors").exists())
				.andExpect(jsonPath("errors.length()").value(1))
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
	@DisplayName("게시물 조회 요청/응답 - 200")
	void getArticle_success() throws Exception {
		//given
		final ArticleDto.Save dto = buildDtoToCreate();
		Mockito.when(articleService.getArticle(anyLong())).thenReturn(dto.toDomain());

		//when
		final ResultActions resultActions = requestGetArticle();

		//then
		final MvcResult mvcResult = resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("title").value(dto.getTitle()))
				.andExpect(jsonPath("content").value(dto.getContent()))
				.andReturn();

		// 아래부터는 참고사항

		// 1. Get Response Body as JSON String
		final String json = mvcResult.getResponse().getContentAsString();

		// 2. Deserialize JSON String to Object
		final ArticleDto.Response deserialized = objectMapper.readValue(json, ArticleDto.Response.class);

		log.info("deserialized : {}", deserialized);
	}

	@Test
	@DisplayName("게시물 조회 요청/응답 - 404, 게시물이 존재하지 않는 경우")
	void getArticle_404() throws Exception {
		//given
		Mockito.when(articleService.getArticle(anyLong())).thenThrow(new ArticleNotFoundException());

		//when
		final ResultActions resultActions = requestGetArticle();

		//then
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());

		// 참고 : org.hamcrest.Matchers 사용 시
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message", is(ErrorCode.ARTICLE_NOT_FOUND.getMessage())))
				.andExpect(jsonPath("code", is(ErrorCode.ARTICLE_NOT_FOUND.getCode())))
				.andExpect(jsonPath("status", is(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value())))
				.andExpect(jsonPath("errors", is(empty())));
	}

	private ResultActions requestGetArticle() throws Exception {
		return mockMvc.perform(get("/articles/{id}", 1)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}


	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200")
	void getArticles_success() throws Exception {
		//given
		final ArticleDto.Save dto = buildDtoToCreate();
		Mockito.when(articleService.getArticles()).thenReturn(List.of(dto.toDomain()));

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0]").exists())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].title").value(dto.getTitle()))
				.andExpect(jsonPath("$[0].content").value(dto.getContent()));

		// 아래부터는 참고사항
		final MvcResult mvcResult = resultActions
				.andExpect(jsonPath("$").isArray()) // 변환: List => net.minidev.json.JSONArray
				.andExpect(jsonPath("$[0]").isMap()) // 변환: Object => LinkedHashMap
				.andExpect(jsonPath("$.[0]").isMap()) // $[0] == $.[0]
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].length()").value(3)) // id, title, content
				.andReturn();

		// 1. Get Response Body as JSON String
		final String json = mvcResult.getResponse().getContentAsString();

		// 2. Deserialize JSON String to Object
		// final List<ArticleDto.Response> deserialized = objectMapper.readValue(json, List.class);
		final List<ArticleDto.Response> deserialized = objectMapper.readValue(json, new TypeReference<>() {}); // 타입 안정성을 위한 Super Type Token 사용

		log.info("deserialized : {}", deserialized);
	}

	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200, 게시물이 하나도 존재하지 않을 경우")
	void getArticles_nothingAtAll() throws Exception {
		//given
		Mockito.when(articleService.getArticles()).thenReturn(Collections.emptyList());

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
	@DisplayName("게시물 업데이트 요청/응답 - 200, 수정 성공")
	void updateArticle_success() throws Exception {
		//given
		final Article article = Article.builder().build();
		final ArticleDto.Save saveDto = buildDtoToUpdate();
		article.updateArticle(saveDto);

		Mockito.when(articleService.updateArticle(anyLong(), any(ArticleDto.Save.class))).thenReturn(article);

		//when
		final ResultActions resultActions =
				mockMvc.perform(postForm("/articles/1/edit", saveDto)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());

//		final ResultActions resultActions =
//				mockMvc.perform(post("/articles/{id}/edit", 1)
//								.param("title", saveDto.getTitle())
//								.param("content", saveDto.getContent())
//								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//								.accept(MediaType.APPLICATION_JSON_UTF8))
//						.andDo(print());
		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("title").value(saveDto.getTitle()))
				.andExpect(jsonPath("content").value(saveDto.getContent()));
	}

	@Test
	@DisplayName("게시물 업데이트 요청/응답 - 400, 입력값이 잘못된 경우 유효성 검사 실패")
	void updateArticle_validation_fail() throws Exception {
		//given

		//when
		final ResultActions resultActions =
				mockMvc.perform(post("/articles/{id}/edit", 1)
								.param("title", "  ")
								.param("content", "")
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
	@DisplayName("게시물 삭제 요청/응답 - 200, 삭제 성공")
	void deleteArticle() throws Exception {
		//given

		//when
		final ResultActions resultActions =
				mockMvc.perform(get("/articles/{id}/delete", 1)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("번 게시물을 삭제하였습니다.")));
	}

	@Test
	@DisplayName("게시물 삭제 요청/응답 - 404, 존재하지 않는 게시물")
	void deleteArticle_404() throws Exception {
		//given
		doThrow(new ArticleNotFoundException()).when(articleService).deleteArticle(anyLong());

		//when
		final ResultActions resultActions =
				mockMvc.perform(get("/articles/{id}/delete", 1)
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