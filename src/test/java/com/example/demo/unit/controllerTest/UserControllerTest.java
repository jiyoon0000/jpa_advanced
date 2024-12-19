package com.example.demo.unit.controllerTest;

import com.example.demo.config.WebConfig;
import com.example.demo.constants.GlobalConstants;
import com.example.demo.controller.UserController;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class})
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        // given
        UserRequestDto requestDto = new UserRequestDto("USER", "test@test.com", "testUser", "password123");

        // when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 회원가입되었습니다."));
    }

    @Test
    @DisplayName("회원가입 실패")
    void signupFail() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        // when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("test@test.com", "password123");
        MockHttpSession session = new MockHttpSession();

        given(userService.loginUser(any(LoginRequestDto.class))).willReturn(new com.example.demo.dto.Authentication(1L, com.example.demo.entity.Role.USER));

        // when, then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공"));
    }

    @Test
    @DisplayName("로그인 실패 - 유효하지 않은 사용자")
    void loginFail() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("invalid@test.com", "wrongpassword");

        given(userService.loginUser(any(LoginRequestDto.class))).willThrow(new IllegalArgumentException("유효하지 않은 사용자 이름 혹은 잘못된 비밀번호"));

        // when, then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("유효하지 않은 사용자 이름 혹은 잘못된 비밀번호"));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(GlobalConstants.USER_AUTH, new com.example.demo.dto.Authentication(1L, com.example.demo.entity.Role.USER));

        // when, then
        mockMvc.perform(post("/users/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃 성공"));
    }

    @Test
    @DisplayName("로그아웃 실패")
    void logoutFail() throws Exception {
        // when, then
        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isOk()) // 로그아웃 시 세션 없어도 OK 처리
                .andExpect(content().string("로그아웃 성공"));
    }
}