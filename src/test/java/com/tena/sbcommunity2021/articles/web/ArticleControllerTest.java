package com.tena.sbcommunity2021.articles.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.global.commons.ResponseData;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import com.tena.sbcommunity2021.test.IntegrationTest;
import com.tena.sbcommunity2021.test.setup.ArticleSetup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
public class ArticleControllerTest extends IntegrationTest {

	@Autowired ArticleSetup articleSetup;

	@BeforeEach
	void setUp() {
		articleSetup.deleteAllArticles();
	}

	@Test
	@DisplayName("신규 게시물 작성 요청/응답 - 201, 정상적으로 작성")
	void createArticle_success() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		resultActions
				.andExpect(status().isCreated()) // 201
				.andExpect(content().string(containsString("게시물이 작성되었습니다.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("message").value(startsWith("게시물이 작성되었습니다.")))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.id").exists())
				.andExpect(jsonPath("body.id", greaterThan(0)))
				.andExpect(jsonPath("body.id").value(greaterThan(0)))
				.andExpect(jsonPath("body.title").value(dto.getTitle()))
				.andExpect(jsonPath("body.content").value(dto.getContent()))
				.andExpect(jsonPath("body.regDate").exists())
				.andExpect(jsonPath("body.updateDate").exists());
	}

	@Test
	@DisplayName("신규 게시물 작성 요청/응답 - 400, 입력값이 잘못된 경우 업데이트 실패 (유효성 검사 실패)")
	void createArticle_validation_fail() throws Exception {
		//given
		final ArticleDto.Save dto = ArticleDto.Save.builder().title("   ").content("내용").build(); // blank (white space only)
		// final ArticleDto.Save dto = ArticleDto.Save.builder().title("").content("내용").build(); // empty
		// final ArticleDto.Save dto = ArticleDto.Save.builder().title(null).content("내용").build(); // null

		//when
		final ResultActions resultActions = requestCreateArticle(dto);

		//then
		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(content().string(containsString("잘못된 입력 값이 포함되어 있습니다.")))
				.andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
				.andExpect(jsonPath("errors").exists())
				.andExpect(jsonPath("errors.length()").value(1));

		// for increasing coverage
		resultActions
				.andExpect(jsonPath("errors[0].field").value("title"))
				.andExpect(jsonPath("errors[0].value").value(dto.getTitle()))
				.andExpect(jsonPath("errors[0].reason").isNotEmpty());
	}
	

	@Test
	@DisplayName("특정 게시물 하나 조회 요청/응답 - 200, 정상적으로 조회")
	void getArticle_success() throws Exception {
		//given
		final Article article = articleSetup.createArticle();

		//when
		final ResultActions resultActions = requestGetArticle(article.getId());

		//then
		final MvcResult mvcResult = resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("번 게시물입니다.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("message").value(endsWith("번 게시물입니다.")))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.id").exists())
				.andExpect(jsonPath("body.title").value(article.getTitle()))
				.andExpect(jsonPath("body.content").value(article.getContent()))
				.andExpect(jsonPath("body.regDate").exists())
				.andExpect(jsonPath("body.updateDate").exists())
				.andReturn();

		// 참고: JSON 역직렬화 (Deserialize JSON String to Object)
		// 1단계. 응답 바디에서 JSON 문자열 추출
		final String json = mvcResult.getResponse().getContentAsString();
		// 2단계. Jackson 라이브러리의 ObjectMapper 를 사용해 역직렬화
		final ArticleDto.Response deserialized = objectMapper.readValue(json, ArticleDto.Response.class);
		// 결과 확인
		log.info("deserialized : {}", deserialized);
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
				.andExpect(content().string(containsString("A-001")))
				.andExpect(content().string(containsString("게시물이 존재하지 않습니다.")))
				.andExpect(jsonPath("message").value(endsWith("게시물이 존재하지 않습니다.")))
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());
	}

	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200, 정상적으로 조회")
	void getArticles_success() throws Exception {
		//given
		final Article article = articleSetup.createArticle();
		final Article article2 = articleSetup.createArticle();

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		// ORDER BY id DESC;
		final MvcResult mvcResult = resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("게시물 목록입니다.")))
				.andExpect(jsonPath("$").isMap()) // ResponseData 타입의 객체
				.andExpect(jsonPath("$.length()").value(5)) // 5개 항목 : resultCode, message, body, fail, success
				.andExpect(jsonPath("$.resultCode").value("S-1"))
				.andExpect(jsonPath("$.message").value(startsWith("게시물 목록입니다.")))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.body").isArray())
				.andExpect(jsonPath("$.body.length()").value(2))
				.andExpect(jsonPath("$.body.[0].id").value(article2.getId()))
				.andExpect(jsonPath("$.body.[0].title").value(article2.getTitle()))
				.andExpect(jsonPath("$.body.[0].content").value(article2.getContent()))
				.andExpect(jsonPath("$.body.[0].regDate").exists())
				.andExpect(jsonPath("$.body.[0].updateDate").exists())
				.andExpect(jsonPath("$.body.[1].id").value(article.getId()))
				.andExpect(jsonPath("$.body.[1].title").value(article.getTitle()))
				.andExpect(jsonPath("$.body.[1].content").value(article.getContent()))
				.andExpect(jsonPath("$.body.[1].regDate").exists())
				.andExpect(jsonPath("$.body.[1].updateDate").exists())
				.andReturn();

		// 참고: JSON 역직렬화 (Deserialize JSON String to Object)
		// 1단계. 응답 바디에서 JSON 문자열 추출
		final String json = mvcResult.getResponse().getContentAsString();
		// 2단계. Jackson 라이브러리의 ObjectMapper 를 사용해 역직렬화
		// final ResponseData deserialized = objectMapper.readValue(json, ResponseData.class);
		final ResponseData<List<ArticleDto.Response>> deserialized = objectMapper.readValue(json, new TypeReference<>() {}); // 타입 안정성을 위한 Super Type Token 사용
		// 결과 확인
		log.info("deserialized : {}", deserialized);
	}

	@Test
	@DisplayName("전체 게시물 조회 요청/응답 - 200, 게시물이 하나도 존재하지 않을 경우")
	void getArticles_nothingAtAll() throws Exception {
		//given

		//when
		final ResultActions resultActions = requestGetAllArticles();

		//then
		resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(jsonPath("$.body").isEmpty()) // 빈 배열 리턴
				.andExpect(jsonPath("$.body").isArray())
				.andExpect(jsonPath("$.body.length()").value(0));
	}

	@Test
	@DisplayName("기존 게시물 업데이트 요청/응답 - 200, 정상적으로 수정")
	void updateArticle_success() throws Exception {
		//given
		final Article article = articleSetup.createArticle(); // 기존 게시물

		//when
		final LocalDateTime updatedAt = LocalDateTime.of(2021, 12, 12, 12, 12);
		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("제목 수정")
				.content("내용 수정")
				.updateDate(updatedAt)
				.build();

		final ResultActions resultActions = requestUpdateArticle(article.getId(), saveDto); // 게시물 업데이트 요청

		//then
		final MvcResult mvcResult = resultActions
				.andExpect(status().isOk()) // 200
				.andExpect(content().string(containsString("번 게시물을 수정하였습니다.")))
				.andExpect(jsonPath("resultCode").value("S-1"))
				.andExpect(jsonPath("message").value(endsWith("번 게시물을 수정하였습니다.")))
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("body.title").value(saveDto.getTitle()))
				.andExpect(jsonPath("body.content").value(saveDto.getContent()))
				.andExpect(jsonPath("body.updateDate", not(jsonPath("body.regDate")))) //업데이트 후 updateDate 수정
				.andReturn();

		assertThat(article.getRegDate()).isNotEqualTo(article.getUpdateDate());

		// for increasing coverage
		final String json = mvcResult.getResponse().getContentAsString();
		final ResponseData<ArticleDto.Response> deserialized = objectMapper.readValue(json, new TypeReference<>() {});
		final ArticleDto.Response body = deserialized.getBody();

		assertThat(body.getUpdateDate()).isNotEqualTo(body.getRegDate());
	}

	@Test
	@DisplayName("기존 게시물 업데이트 요청/응답 - 400, 입력값이 누락/잘못된 경우 업데이트 실패 (유효성 검사 실패)")
	void updateArticle_validation_fail() throws Exception {
		//given
		final Article article = articleSetup.createArticle();

		final ArticleDto.Save req = ArticleDto.Save.builder()
				.title("  ") // blank (white space only)
				.content(null)
				.build();

		//when
		final ResultActions resultActions = requestUpdateArticle(article.getId(), req);

		//then
		resultActions
				.andExpect(status().isBadRequest()) // 400
				.andExpect(content().string(containsString("잘못된 입력 값이 포함되어 있습니다.")))
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
		final Article article = articleSetup.createArticle();

		//when
		final ResultActions resultActions = requestDeleteArticle(article.getId());

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
		final ResultActions resultActions = requestDeleteArticle(Long.MAX_VALUE);

		//then
		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
				.andExpect(jsonPath("code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()))
				.andExpect(jsonPath("status").value(ErrorCode.ARTICLE_NOT_FOUND.getStatus().value()))
				.andExpect(jsonPath("errors").isEmpty());
	}

	private ResultActions requestGetArticle(Long id) throws Exception {
		return mockMvc.perform(get("/articles/{id}", id)
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
	}

	private ResultActions requestUpdateArticle(Long id, ArticleDto.Save dto) throws Exception {
		return mockMvc.perform(post("/articles/{id}/edit", id)
						.param("title", dto.getTitle())
						.param("content", dto.getContent())
						.param("updateDate", String.valueOf(dto.getUpdateDate()))
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}

	private ResultActions requestDeleteArticle(Long id) throws Exception {
		return mockMvc.perform(get("/articles/{id}/delete", id)
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print());
	}

}