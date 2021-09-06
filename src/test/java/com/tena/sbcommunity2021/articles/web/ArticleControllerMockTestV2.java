package com.tena.sbcommunity2021.articles.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;
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

	@SpyBean
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper.getConfiguration()
				.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
				.setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
	}

	@Test
	@DisplayName("게시물 작성 요청/응답 - 201, 작성 성공 시")
	void createArticle_success() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();

		final LocalDateTime createdAt = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article = Article.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.id(1L)
				.regDate(createdAt)
				.updateDate(createdAt)
				.build();

		when(articleService.createArticle(any())).thenReturn(article);

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		verify(articleService, times(1)).createArticle(any());
		resultActions
				.andExpect(status().isCreated()) // 201
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("title").value(dto.getTitle()))
				.andExpect(jsonPath("content").value(dto.getContent()))
				.andExpect(jsonPath("regDate").exists())
				.andExpect(jsonPath("updateDate").exists());
	}

	@Test
	@DisplayName("게시물 작성 요청/응답 - 400, 데이터 무결성 위반 예외 발생 시 / DataIntegrityViolationException")
	void createArticle_fail_DataIntegrityViolationException() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();

		when(articleService.createArticle(any())).thenThrow(DataIntegrityViolationException.class);

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		verify(articleService, times(1)).createArticle(any());

		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());
	}

	@Test
	@DisplayName("게시물 작성 요청/응답 - 400, 입력값이 잘못되어 유효성 검사 실패 시")
	void createArticle_validation_fail() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("   ") // Blank (white space only)
				.content("내용")
				.build();
		//final ArticleDto.Save dto = ArticleDto.Save.builder().title(null).content("내용").build(); // Null
		//final ArticleDto.Save dto = ArticleDto.Save.builder().title("").content("내용").build(); // Empty

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		verify(articleService, times(0)).createArticle(any());

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
				.andExpect(jsonPath("errors[0].value").value(dto.getTitle()))
				.andExpect(jsonPath("errors[0].reason").isNotEmpty());
	}

	@Test
	@DisplayName("게시물 조회 요청/응답 - 200, 조회 성공 시")
	void getArticle_success() throws Exception {
		//given
		final LocalDateTime createdAt = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article = Article.builder()
				.title("제목")
				.content("내용")
				.id(1L)
				.regDate(createdAt)
				.updateDate(createdAt)
				.build();

		when(articleService.getArticle(anyLong())).thenReturn(article);

		//when
		final ResultActions resultActions = requestGetArticle();

		//then
		verify(articleService, atLeastOnce()).getArticle(anyLong());

		final MvcResult mvcResult = resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("title").value(article.getTitle()))
				.andExpect(jsonPath("content").value(article.getContent()))
				.andExpect(jsonPath("regDate").exists())
				.andExpect(jsonPath("updateDate").exists())
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
		when(articleService.getArticle(anyLong())).thenThrow(new ArticleNotFoundException());

		//when
		final ResultActions resultActions = requestGetArticle();

		//then
		verify(articleService, atLeastOnce()).getArticle(anyLong());

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

	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200, 조회 성공 시")
	void getArticles_success() throws Exception {
		//given
		final LocalDateTime createdAt1 = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article1 = Article.builder()
				.title("제목 1")
				.content("내용 1")
				.id(1L)
				.regDate(createdAt1)
				.updateDate(createdAt1)
				.build();

		final LocalDateTime createdAt2 = createdAt1.plusYears(1).plusMonths(1).plusDays(1).plusHours(1).plusMinutes(1).plusSeconds(1);
		final Article article2 = Article.builder()
				.title("제목 2")
				.content("내용 2")
				.id(2L)
				.regDate(createdAt1)
				.updateDate(createdAt1)
				.build();

		when(articleService.getArticles()).thenReturn(List.of(article1, article2));

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		verify(articleService, times(1)).getArticles();

		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0]").exists())
				.andExpect(jsonPath("$[0].title").value(article1.getTitle()))
				.andExpect(jsonPath("$[0].content").value(article1.getContent()))
				.andExpect(jsonPath("$[0].regDate").exists())
				.andExpect(jsonPath("$[0].updateDate").exists())
				.andExpect(jsonPath("$[1]").exists())
				.andExpect(jsonPath("$[1].title").value(article2.getTitle()))
				.andExpect(jsonPath("$[1].content").value(article2.getContent()))
				.andExpect(jsonPath("$[1].regDate").exists())
				.andExpect(jsonPath("$[1].updateDate").exists());

		// 아래부터는 참고사항
		final MvcResult mvcResult = resultActions
				.andExpect(jsonPath("$").isArray()) // 변환: List => net.minidev.json.JSONArray
				.andExpect(jsonPath("$[0]").isMap()) // 변환: Object => LinkedHashMap
				.andExpect(jsonPath("$.[0]").isMap()) // $[0] == $.[0]
				.andExpect(jsonPath("$.length()").value(2)) // article1, article2
				.andExpect(jsonPath("$[0].length()").value(5)) // id, title, content, regDate, updateDate
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
		when(articleService.getArticles()).thenReturn(Collections.emptyList()); // 비어있는 리스트 반환

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@DisplayName("게시물 업데이트 요청/응답 - 200, 수정 성공 시")
	@Test
	void updateArticle_success() throws Exception {
		//given
		final LocalDateTime createdAt = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article = Article.builder()
				.title("기존 제목")
				.content("기존 내용")
				.id(1L)
				.regDate(createdAt)
				.updateDate(createdAt)
				.build();

		final LocalDateTime updatedAt = createdAt.plusYears(1).plusMonths(1).plusDays(1).plusHours(1).plusMinutes(1).plusSeconds(1);
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("제목 수정") // 제목만 변경
				.content(article.getContent()) // 내용은 그대로
				.updateDate(updatedAt)
				.build();

		when(articleService.updateArticle(anyLong(), any())).thenAnswer(invocation -> {
			article.setTitle(dto.getTitle());
			article.setContent(dto.getContent());
			article.setUpdateDate(dto.getUpdateDate());
			return article;
		});

		//when
		final ResultActions resultActions = requestUpdateArticle(dto);

		//then
		verify(articleService, times(1)).updateArticle(anyLong(), any());
		verify(modelMapper, times(1)).map(any(), any());

		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("title").value(dto.getTitle()))
				.andExpect(jsonPath("content").value(dto.getContent()))
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("regDate").exists())
				.andExpect(jsonPath("updateDate").exists())
				.andReturn();

		//for increasing coverage
		assertThat(article.getRegDate()).isNotEqualTo(article.getUpdateDate());
	}

	@DisplayName("게시물 업데이트 요청/응답 - 400, 입력값이 잘못되어 유효성 검사 실패 시")
	@Test
	void updateArticle_validation_fail() throws Exception {
		//when
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("   ") // blank (white space only)
				.content("") // empty
				.build();

		final ResultActions resultActions = requestUpdateArticle(dto);

		//then
		verify(articleService, times(0)).updateArticle(anyLong(), any()); // 서비스 메서드까지 가지 않음

		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").exists())
				.andExpect(jsonPath("errors.length()").value(2));
	}

	@DisplayName("게시물 삭제 요청/응답 - 200, 삭제 성공 시")
	@Test
	void deleteArticle() throws Exception {
		//given

		//when
		final ResultActions resultActions = requestDeleteArticle();

		//then
		verify(articleService, times(1)).deleteArticle(anyLong());

		resultActions
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("번 게시물을 삭제하였습니다.")));
	}

	@DisplayName("게시물 삭제 요청/응답 - 404, 존재하지 않는 게시물 삭제 시")
	@Test
	void deleteArticle_404() throws Exception {
		//given
		doThrow(new ArticleNotFoundException()).when(articleService).deleteArticle(anyLong());

		//when
		final ResultActions resultActions = requestDeleteArticle();

		//then
		verify(articleService, times(1)).deleteArticle(anyLong());

		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());
	}


	private ResultActions requestGetArticle() throws Exception {
		return mockMvc.perform(get("/articles/{id}", Long.MAX_VALUE)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}

	private ResultActions requestGetAllArticles() throws Exception {
		return mockMvc.perform(get("/articles")
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}

	private ResultActions requestCreateArticle(ArticleDto.Save dto) throws Exception {
		return mockMvc.perform(postForm("/articles/new", dto)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());

//		return mockMvc.perform(post("/articles/new")
//						.param("title", dto.getTitle())
//						.param("content", dto.getContent())
//						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//						.accept(MediaType.APPLICATION_JSON_UTF8))
//				.andDo(print());
	}

	private ResultActions requestUpdateArticle(ArticleDto.Save dto) throws Exception {
		final Long id = Long.MAX_VALUE;
		final String url = String.format("/articles/%d/edit", id);

		return mockMvc.perform(postForm(url, dto)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());

//		return mockMvc.perform(post("/articles/{id}/edit", Long.MAX_VALUE)
//						.param("title", dto.getTitle())
//						.param("content", dto.getContent())
//						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//						.accept(MediaType.APPLICATION_JSON_UTF8))
//				.andDo(print());
	}

	private ResultActions requestDeleteArticle() throws Exception {
		return mockMvc.perform(get("/articles/{id}/delete", Long.MAX_VALUE)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}

}