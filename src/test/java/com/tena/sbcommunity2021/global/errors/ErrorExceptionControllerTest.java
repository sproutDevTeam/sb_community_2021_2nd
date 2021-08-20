package com.tena.sbcommunity2021.global.errors;

import com.tena.sbcommunity2021.test.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorExceptionControllerTest extends IntegrationTest {

    @Test
    @DisplayName("지원하지 않는 HTTP Method 호출 했을 경우 / HttpRequestMethodNotSupportedException")
    void handleHttpRequestMethodNotSupportedException_test() throws Exception {
        // given

        // when
        // 지원하지 않는 post 요청으로
        final ResultActions resultActions = mockMvc.perform(post("/articles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_NOT_ALLOWED.getMessage()))
                .andExpect(jsonPath("$.status").value(ErrorCode.METHOD_NOT_ALLOWED.getStatus().value()))
                .andExpect(jsonPath("$.error").value(ErrorCode.METHOD_NOT_ALLOWED.getStatus().name()))
                .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_NOT_ALLOWED.getCode()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("입력값 바인딩 에러 발생시 / BindException")
    public void handleBindException_test() throws Exception {
        //given

        //when
        final ResultActions resultActions =
                mockMvc.perform(get("/articles/new")
                                .param("asdfasdf", "asdfkansldflaksdfnl")
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
                .andExpect(jsonPath("status").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().value()))
                .andExpect(jsonPath("error").value(ErrorCode.INVALID_INPUT_VALUE.getStatus().name()))
                .andExpect(jsonPath("code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("errors").exists())
                .andExpect(jsonPath("errors").isNotEmpty());
//        resultActions
//                .andExpect(jsonPath("errors[0].field").value("title"))
//                .andExpect(jsonPath("errors[0].value").value("null"))
//                .andExpect(jsonPath("errors[0].reason").value("must not be blank"))
//                .andExpect(jsonPath("errors[1].field").value("content"))
//                .andExpect(jsonPath("errors[1].value").value("null"))
//                .andExpect(jsonPath("errors[1].reason").value("must not be blank"));

    }


}