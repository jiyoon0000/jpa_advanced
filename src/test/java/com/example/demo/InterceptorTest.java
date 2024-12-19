package com.example.demo;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.Authentication;
import com.example.demo.entity.Role;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.interceptor.AdminRoleInterceptor;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.interceptor.UserRoleInterceptor;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterceptorTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HttpSession mockSession;

    private AuthInterceptor authInterceptor;
    private AdminRoleInterceptor adminRoleInterceptor;
    private UserRoleInterceptor userRoleInterceptor;

    @BeforeEach
    void setUp(){
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockSession = mock(HttpSession.class);

        authInterceptor = new AuthInterceptor();
        adminRoleInterceptor = new AdminRoleInterceptor();
        userRoleInterceptor = new UserRoleInterceptor();
    }

    @Test
    @DisplayName("AuthInterceptor 성공")
    void authInterceptorSuccess() throws Exception{
        //given
        Authentication auth = new Authentication(1L, Role.USER);
        request.setSession(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH)).thenReturn(auth);

        //when, then
        boolean result = authInterceptor.preHandle(request,response,new Object());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("AuthInterceptor - no session")
    void authInterceptorFailNoSession(){
        //given
        request.setSession(null);

        //when, then
        assertThatThrownBy(() -> authInterceptor.preHandle(request, response, new Object()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인이 필요합니다.");
    }

    @Test
    @DisplayName("AuthInterceptor - no auth")
    void authInterceptorFailNoAuth(){
        //given
        request.setSession(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH)).thenReturn(null);

        //when, then
        assertThatThrownBy(()-> authInterceptor.preHandle(request,response,new Object()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인이 필요합니다.");
    }

    @Test
    @DisplayName("AdminRoleInterceptor 성공")
    void adminRoleInterceptorSuccess() throws Exception{
        //given
        Authentication auth = new Authentication(1L, Role.ADMIN);
        request.setSession(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH)).thenReturn(auth);

        //when, then
        boolean result = adminRoleInterceptor.preHandle(request,response,new Object());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("AdminRoleInterceptor 권한없음")
    void adminRoleInterceptorFail(){
        //given
        Authentication auth = new Authentication(1L, Role.USER);
        request.setSession(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH)).thenReturn(auth);

        //when, then
        assertThatThrownBy(()-> adminRoleInterceptor.preHandle(request,response,new Object()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("ADMIN 권한이 필요합니다.");
    }

    @Test
    @DisplayName("UserRoleInterceptor 성공")
    void UserRoleInterceptorSuccess() throws Exception{
        //given
        Authentication auth = new Authentication(1L, Role.USER);
        request.setSession(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH)).thenReturn(auth);

        //when, then
        boolean result = userRoleInterceptor.preHandle(request,response,new Object());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("UserRoleInterceptor 권한없음")
    void UserRoleInterceptorFail(){
        //given
        Authentication auth = new Authentication(1L, Role.ADMIN);
        request.setSession(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH)).thenReturn(auth);

        //when, then
        assertThatThrownBy(()-> userRoleInterceptor.preHandle(request,response,new Object()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("user 권한이 필요합니다.");
    }
}
