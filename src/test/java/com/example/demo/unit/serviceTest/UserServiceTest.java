package com.example.demo.unit.serviceTest;

import com.example.demo.dto.Authentication;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp(){
        mockUser = new User("USER", "test@test.com", "TestUser", "encodedPassword");
    }

    @Test
    @DisplayName("회원 가입 성공")
    void signupSuccess(){
        //given
        UserRequestDto userRequestDto = new UserRequestDto("USER", "test@test.com", "TestUser", "password123");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        //when
        userService.signupWithEmail(userRequestDto);

        //then
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(userRequestDto.getPassword()).isNotEqualTo("password123");
    }

    @Test
    @DisplayName("로그인 성공")
    void loginUserSuccess(){
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@test.com", "password123");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(mockUser);

        try(MockedStatic<PasswordEncoder> mockedEncoder = mockStatic(PasswordEncoder.class)){
            mockedEncoder.when(() -> PasswordEncoder.matches(loginRequestDto.getPassword(), mockUser.getPassword()))
                    .thenReturn(true);

            //when
            Authentication auth = userService.loginUser(loginRequestDto);

            //then
            assertThat(auth).isNotNull();
            assertThat(auth.getId()).isEqualTo(mockUser.getId());
            assertThat(auth.getRole()).isEqualTo(mockUser.getRole());
        }
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void loginFailNotFound(){
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("notfound@test.com", "password123");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(null);

        //when, then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> userService.loginUser(loginRequestDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exception.getReason()).isEqualTo("유효하지 않은 사용자 이름 혹은 잘못된 비밀번호");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailPasswordMismatch() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@test.com", "wrongPassword");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(mockUser);

        try (MockedStatic<PasswordEncoder> mockedEncoder = mockStatic(PasswordEncoder.class)) {
            mockedEncoder.when(() -> PasswordEncoder.matches(loginRequestDto.getPassword(), mockUser.getPassword())).thenReturn(false);

            // when & then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> userService.loginUser(loginRequestDto));

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(exception.getReason()).isEqualTo("유효하지 않은 사용자 이름 혹은 잘못된 비밀번호");
        }
    }
}
