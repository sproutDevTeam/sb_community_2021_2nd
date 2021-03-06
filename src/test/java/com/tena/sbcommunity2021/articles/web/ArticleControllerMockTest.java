package com.tena.sbcommunity2021.articles.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import com.tena.sbcommunity2021.global.errors.ErrorExceptionController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ArticleControllerMockTest {

	@InjectMocks
	private ArticleController articleController;

	@Mock
	private ArticleService articleService;

	@Spy
	private ModelMapper modelMapper;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(articleController)
				.setControllerAdvice(new ErrorExceptionController())
				.build();
		objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
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

		final Article article = mock(Article.class);
		doReturn(1L).when(article).getId();
		doReturn(dto.getTitle()).when(article).getTitle();
		doReturn(dto.getContent()).when(article).getContent();
		final LocalDateTime regDate = LocalDateTime.of(2021, 11, 11, 11, 11);
		doReturn(regDate).when(article).getRegDate();
		doReturn(regDate).when(article).getUpdateDate();

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
		final Article article = mock(Article.class);
		doReturn(1L).when(article).getId();
		doReturn("??????").when(article).getTitle();
		doReturn("??????").when(article).getContent();
		doReturn(LocalDateTime.now()).when(article).getRegDate();
		doReturn(LocalDateTime.now()).when(article).getUpdateDate();

		when(articleService.getArticle(anyLong())).thenReturn(article);

		//when
		final ResultActions resultActions = requestGetArticle();

		//then
		verify(articleService, atLeastOnce()).getArticle(anyLong());

		resultActions
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
	}

	@Test
	@DisplayName("?????? ????????? ?????? ??????/?????? - 200, ?????? ?????? ???")
	void getArticles_success() throws Exception {
		//given
		final Article article1 = mock(Article.class);
		doReturn(1L).when(article1).getId();
		doReturn("?????? 1").when(article1).getTitle();
		doReturn("?????? 1").when(article1).getContent();
		doReturn(LocalDateTime.now()).when(article1).getRegDate();
		doReturn(LocalDateTime.now()).when(article1).getUpdateDate();

		final Article article2 = mock(Article.class);
		doReturn(2L).when(article2).getId();
		doReturn("?????? 2").when(article2).getTitle();
		doReturn("?????? 2").when(article2).getContent();
		doReturn(LocalDateTime.now()).when(article2).getRegDate();
		doReturn(LocalDateTime.now()).when(article2).getUpdateDate();

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

	@Test
	@DisplayName("????????? ???????????? ??????/?????? - 200, ?????? ?????? ???")
	void updateArticle_success() throws Exception {
		//given
		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("????????? ??????")
				.content("????????? ??????")
				.build();

		final Article article = mock(Article.class);
		final LocalDateTime regDate = LocalDateTime.of(2020, 11, 11, 11, 30, 0);
		final LocalDateTime updateDate = LocalDateTime.now();
		when(article.getId()).thenReturn(1L);
		when(article.getTitle()).thenReturn(saveDto.getTitle());
		when(article.getContent()).thenReturn(saveDto.getContent());
		when(article.getRegDate()).thenReturn(regDate);
		when(article.getUpdateDate()).thenReturn(updateDate);

		when(articleService.updateArticle(anyLong(), any())).thenReturn(article);

		//when
		final ResultActions resultActions = requestUpdateArticle(saveDto);

		//then
		verify(articleService, times(1)).updateArticle(anyLong(), any());

		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("??? ???????????? ?????????????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.title").value(saveDto.getTitle()))
				.andExpect(jsonPath("body.content").value(saveDto.getContent()))
				.andExpect(jsonPath("body.updateDate", not(jsonPath("body.regDate")))) //???????????? ??? updateDate ??????
				.andReturn();
	}

	@Test
	@DisplayName("[?????? 1] ????????? ???????????? ??????/?????? - 200, ?????? ?????? ???")
	void updateArticle_success_????????????_???????????????_??????() throws Exception {
		//given
		when(articleService.updateArticle(anyLong(), any())).thenAnswer(invocation -> { // articleService.updateArticle ????????? ?????? ???
			final ArticleDto.Save argument = invocation.getArgument(1, ArticleDto.Save.class); // ????????? ??????????????? any() ??????

			final Article article = mock(Article.class);
			final LocalDateTime regDate = LocalDateTime.of(2020, 11, 11, 11, 30, 0);
			final LocalDateTime updateDate = LocalDateTime.now();

			when(article.getId()).thenReturn(1L);
			when(article.getTitle()).thenReturn(argument.getTitle());
			when(article.getContent()).thenReturn(argument.getContent());
			when(article.getRegDate()).thenReturn(regDate);
			when(article.getUpdateDate()).thenReturn(updateDate);

			return article;
		});

		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("????????? ??????")
				.content("????????? ??????")
				.build();

		//when
		final ResultActions resultActions = requestUpdateArticle(saveDto);

		//then
		verify(articleService, times(1)).updateArticle(anyLong(), any());

		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("??? ???????????? ?????????????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.title").value(saveDto.getTitle()))
				.andExpect(jsonPath("body.content").value(saveDto.getContent()))
				.andExpect(jsonPath("body.updateDate", not(jsonPath("body.regDate")))) //???????????? ??? updateDate ??????
				.andReturn();
	}

	@Test
	@DisplayName("[?????? 2] ????????? ???????????? ??????/?????? - 200, ?????? ?????? ???")
	void updateArticle_success_???????????????_??????_???????????????() throws Exception {
		//given
		final Article article = mock(Article.class);
		when(article.getId()).thenReturn(1L);
		when(article.getTitle()).thenReturn("?????? ??????");
		when(article.getContent()).thenReturn("?????? ??????");

		LocalDateTime createdAt = LocalDateTime.of(2020, 11, 11, 11, 30, 0);

		when(article.getRegDate()).thenReturn(createdAt);
		when(article.getUpdateDate()).thenReturn(createdAt);

		when(articleService.updateArticle(anyLong(), any())).thenAnswer(invocation -> { // articleService.updateArticle ????????? ?????? ???
			final ArticleDto.Save argument = invocation.getArgument(1, ArticleDto.Save.class); // ????????? ??????????????? any() ??????

			LocalDateTime updatedAt = LocalDateTime.now();
			when(article.getUpdateDate()).thenReturn(updatedAt);

			if(!argument.getTitle().isEmpty())
				when(article.getTitle()).thenReturn(argument.getTitle());
			if(!argument.getContent().isEmpty())
				when(article.getContent()).thenReturn(argument.getContent());

			return article;
		});

		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("????????? ??????") // ????????? ??????
				.content(article.getContent()) // ????????? ????????????
				.build();

		//when
		final ResultActions resultActions = requestUpdateArticle(saveDto);

		//then
		verify(articleService, times(1)).updateArticle(anyLong(), any());

		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("??? ???????????? ?????????????????????.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.title").value(saveDto.getTitle()))
				.andExpect(jsonPath("body.content").value(saveDto.getContent()))
				.andExpect(jsonPath("body.updateDate", not(jsonPath("body.regDate")))) //???????????? ??? updateDate ??????
				.andReturn();

		// for increasing coverage
		assertThat(article.getRegDate()).isNotEqualTo(article.getUpdateDate());
	}

	@Test
	@DisplayName("????????? ???????????? ??????/?????? - 400, ???????????? ???????????? ????????? ?????? ?????? ???")
	void updateArticle_validation_fail() throws Exception {
		//given

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

	@Test
	@DisplayName("????????? ?????? ??????/?????? - 200, ?????? ?????? ???")
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

	@Test
	@DisplayName("????????? ?????? ??????/?????? - 404, ???????????? ?????? ????????? ?????? ???")
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

		return mockMvc.perform(postForm("/articles/"+ id +"/edit", dto)
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