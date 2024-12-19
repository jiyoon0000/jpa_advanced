package com.example.demo;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.Authentication;
import com.example.demo.entity.Role;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.filter.AuthFilter;
import com.example.demo.filter.RoleFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FilterTest {

    //AuthFilter와 RoleFilter 동작 확인
    private AuthFilter authFilter;
    private RoleFilter roleFilter;

    //임시 객체 생성
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;

    //filterChain을 통해 다음 필터로 요청이 전달되는지 확인
    private FilterChain mockFilterChain;

    //각 테스트 실행 전에 호출
    @BeforeEach
    void setUp() {
        authFilter = new AuthFilter();
        roleFilter = new RoleFilter(Role.USER);
        mockRequest = mock(HttpServletRequest.class);
        mockSession = mock(HttpSession.class);
        mockFilterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("필터 체인 성공")
    void filterChainSuccess() throws Exception {
        // Given
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH))
                .thenReturn(new Authentication(1L, Role.USER));

        // When & Then
        //doFilter로 다음 대상으로 요청 전달
        authFilter.doFilter(mockRequest, mock(HttpServletResponse.class), mockFilterChain);
        roleFilter.doFilter(mockRequest, mock(HttpServletResponse.class), mockFilterChain);

        verify(mockFilterChain, times(2))
                .doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("필터 체인 실패 - 세션 없음")
    void filterChainFailNoSession() {
        // Given
        when(mockRequest.getSession(false)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authFilter.doFilter(mockRequest, mock(ServletResponse.class), mockFilterChain))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인 필요");
    }

    @Test
    @DisplayName("필터 체인 실패 - 잘못된 역할")
    void filterChainFailUnauthorizedRole() {
        // Given
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute(GlobalConstants.USER_AUTH))
                .thenReturn(new Authentication(1L, Role.ADMIN)); // 권한 불일치

        // When & Then
        //assertThatThrownBy : 예외가 발생했는지, 예외 메시지가 올바른지
        assertThatThrownBy(() -> roleFilter.doFilter(mockRequest, mock(ServletResponse.class), mockFilterChain))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("권한이 필요합니다.");
    }
}
