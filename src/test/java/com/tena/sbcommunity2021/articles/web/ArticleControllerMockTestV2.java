package com.tena.sbcommunity2021.articles.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.commons.ResponseData;
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
	@DisplayName("????????? ?????? ??????/?????? - 201, ?????? ?????? ???")
	void createArticle_success() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("??????")
				.content("??????")
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
				.andExpect(content().string(containsString("???????????? ?????????????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("message").value(org.hamcrest.Matchers.startsWith("???????????? ?????????????????????.")))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.id").exists())
				.andExpect(jsonPath("body.title").value(dto.getTitle()))
				.andExpect(jsonPath("body.content").value(dto.getContent()))
				.andExpect(jsonPath("body.regDate").exists())
				.andExpect(jsonPath("body.updateDate").exists());
	}

	@Test
	@DisplayName("????????? ?????? ??????/?????? - 400, ????????? ????????? ?????? ?????? ?????? ??? / DataIntegrityViolationException")
	void createArticle_fail_DataIntegrityViolationException() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("??????")
				.content("??????")
				.build();

		when(articleService.createArticle(any())).thenThrow(DataIntegrityViolationException.class);

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		verify(articleService, times(1)).createArticle(any());

		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(content().string(containsString("????????? ?????? ?????? ???????????? ????????????.")))
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());
	}

	@Test
	@DisplayName("????????? ?????? ??????/?????? - 400, ???????????? ???????????? ????????? ?????? ?????? ???")
	void createArticle_validation_fail() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("   ") // Blank (white space only)
				.content("??????")
				.build();
		//final ArticleDto.Save dto = ArticleDto.Save.builder().title(null).content("??????").build(); // Null
		//final ArticleDto.Save dto = ArticleDto.Save.builder().title("").content("??????").build(); // Empty

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		verify(articleService, times(0)).createArticle(any());

		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(content().string(containsString("????????? ?????? ?????? ???????????? ????????????.")))
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
	@DisplayName("????????? ?????? ??????/?????? - 200, ?????? ?????? ???")
	void getArticle_success() throws Exception {
		//given
		final LocalDateTime createdAt = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article = Article.builder()
				.title("??????")
				.content("??????")
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
				.andExpect(content().string(containsString("??? ??????????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.id").exists())
				.andExpect(jsonPath("body.title").value(article.getTitle()))
				.andExpect(jsonPath("body.content").value(article.getContent()))
				.andExpect(jsonPath("body.regDate").exists())
				.andExpect(jsonPath("body.updateDate").exists())
				.andReturn();

		// ??????????????? ????????????

		// 1. Get Response Body as JSON String
		final String json = mvcResult.getResponse().getContentAsString();

		// 2. Deserialize JSON String to Object
		// final ResponseData deserialized = objectMapper.readValue(json, ResponseData.class);
		final ResponseData<ArticleDto.Response> deserialized = objectMapper.readValue(json, new TypeReference<>() {});
		final ArticleDto.Response body = deserialized.getBody();
		log.info("deserialized : {}", deserialized);
		log.info("body : {}", body);
	}

	@Test
	@DisplayName("????????? ?????? ??????/?????? - 404, ???????????? ???????????? ?????? ??????")
	void getArticle_404() throws Exception {
		//given
		when(articleService.getArticle(anyLong())).thenThrow(new ArticleNotFoundException());

		//when
		final ResultActions resultActions = requestGetArticle();

		//then
		verify(articleService, atLeastOnce()).getArticle(anyLong());

		resultActions
				.andExpect(status().isNotFound()) // 404
				.andExpect(content().string(containsString("???????????? ???????????? ????????????.")))
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());

		// ?????? : org.hamcrest.Matchers ?????? ???
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message", is(ErrorCode.ARTICLE_NOT_FOUND.getMessage())))
				.andExpect(jsonPath("code", is(ErrorCode.ARTICLE_NOT_FOUND.getCode())))
				.andExpect(jsonPath("status", is(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value())))
				.andExpect(jsonPath("errors", is(empty())));
	}

	@Test
	@DisplayName("?????? ????????? ?????? ??????/?????? - 200, ?????? ?????? ???")
	void getArticles_success() throws Exception {
		//given
		final LocalDateTime createdAt1 = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article1 = Article.builder()
				.title("?????? 1")
				.content("?????? 1")
				.id(1L)
				.regDate(createdAt1)
				.updateDate(createdAt1)
				.build();

		final LocalDateTime createdAt2 = createdAt1.plusYears(1).plusMonths(1).plusDays(1).plusHours(1).plusMinutes(1).plusSeconds(1);
		final Article article2 = Article.builder()
				.title("?????? 2")
				.content("?????? 2")
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
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("????????? ???????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body").isArray())
				.andExpect(jsonPath("body.length()").value(2))
				.andExpect(jsonPath("body.[0].title").value(article1.getTitle()))
				.andExpect(jsonPath("body.[0].content").value(article1.getContent()))
				.andExpect(jsonPath("body.[0].regDate").exists())
				.andExpect(jsonPath("body.[0].updateDate").exists())
				.andExpect(jsonPath("body.[1].title").value(article2.getTitle()))
				.andExpect(jsonPath("body.[1].content").value(article2.getContent()))
				.andExpect(jsonPath("body.[1].regDate").exists())
				.andExpect(jsonPath("body.[1].updateDate").exists());

		// ??????????????? ????????????
		final MvcResult mvcResult = resultActions
				.andExpect(jsonPath("$").isMap()) // ResponseData ????????? ??????
				.andExpect(jsonPath("$.length()").value(5)) // 5??? ?????? : resultCode, message, body, fail, success
				.andExpect(jsonPath("$.body").isArray())
				.andExpect(jsonPath("$.body.length()").value(2)) // article1, article2
				.andExpect(jsonPath("$.body.[0]").exists()) // article1
				.andExpect(jsonPath("$.body.[0]").isMap())
				.andExpect(jsonPath("$.body.[0].length()").value(5)) // id, title, content, regDate, updateDate
				.andExpect(jsonPath("$.body.[1]").exists()) // article2
				.andExpect(jsonPath("$.body.[1]").isMap())
				.andExpect(jsonPath("$.body.[2]").doesNotExist())
				.andReturn();

		// 1. Get Response Body as JSON String
		final String json = mvcResult.getResponse().getContentAsString();

		// 2. Deserialize JSON String to Object
		// final ResponseData deserialized = objectMapper.readValue(json, ResponseData.class);
		final ResponseData<List<ArticleDto.Response>> deserialized = objectMapper.readValue(json, new TypeReference<>() {}); // ?????? ???????????? ?????? Super Type Token ??????

		log.info("deserialized : {}", deserialized);
	}

	@Test
	@DisplayName("?????? ????????? ?????? ??????/?????? - 200, ???????????? ????????? ???????????? ?????? ??????")
	void getArticles_nothingAtAll() throws Exception {
		//given
		when(articleService.getArticles()).thenReturn(Collections.emptyList()); // ???????????? ????????? ??????

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(jsonPath("$.body").isEmpty()) // ??? ?????? ??????
				.andExpect(jsonPath("$.body").isArray())
				.andExpect(jsonPath("$.body.length()").value(0));
	}

	@DisplayName("????????? ???????????? ??????/?????? - 200, ?????? ?????? ???")
	@Test
	void updateArticle_success() throws Exception {
		//given
		final LocalDateTime createdAt = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article article = Article.builder()
				.title("?????? ??????")
				.content("?????? ??????")
				.id(1L)
				.regDate(createdAt)
				.updateDate(createdAt)
				.build();

		final LocalDateTime updatedAt = createdAt.plusYears(1).plusMonths(1).plusDays(1).plusHours(1).plusMinutes(1).plusSeconds(1);
		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("?????? ??????") // ????????? ??????
				.content(article.getContent()) // ????????? ?????????
				.updateDate(updatedAt)
				.build();

		when(articleService.updateArticle(anyLong(), any())).thenAnswer(invocation -> {
			article.setTitle(saveDto.getTitle());
			article.setContent(saveDto.getContent());
			article.setUpdateDate(saveDto.getUpdateDate());
			return article;
		});

		//when
		final ResultActions resultActions = requestUpdateArticle(saveDto);

		//then
		verify(articleService, times(1)).updateArticle(anyLong(), any());
		verify(modelMapper, times(1)).map(any(), any());

		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("??? ???????????? ?????????????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.title").value(saveDto.getTitle()))
				.andExpect(jsonPath("body.content").value(saveDto.getContent()))
				.andExpect(jsonPath("body.updateDate", not(jsonPath("body.regDate")))) //???????????? ??? updateDate ??????
				.andReturn();

		//for increasing coverage
		assertThat(article.getRegDate()).isNotEqualTo(article.getUpdateDate());
	}

	@DisplayName("????????? ???????????? ??????/?????? - 400, ???????????? ???????????? ????????? ?????? ?????? ???")
	@Test
	void updateArticle_validation_fail() throws Exception {
		//when
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("   ") // blank (white space only)
				.content("") // empty
				.build();

		final ResultActions resultActions = requestUpdateArticle(dto);

		//then
		verify(articleService, times(0)).updateArticle(anyLong(), any()); // ????????? ??????????????? ?????? ??????

		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(content().string(containsString("????????? ?????? ?????? ???????????? ????????????.")))
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").exists())
				.andExpect(jsonPath("errors.length()").value(2));
	}

	@DisplayName("????????? ?????? ??????/?????? - 200, ?????? ?????? ???")
	@Test
	void deleteArticle() throws Exception {
		//given

		//when
		final ResultActions resultActions = requestDeleteArticle();

		//then
		verify(articleService, times(1)).deleteArticle(anyLong());

		resultActions
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("??? ???????????? ?????????????????????.")));
	}

	@DisplayName("????????? ?????? ??????/?????? - 404, ???????????? ?????? ????????? ?????? ???")
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