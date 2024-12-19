package com.example.demo.integration;

import com.example.demo.dto.Authentication;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess(){
        //given
        String role = "USER";
        String email = "test@test.com";
        String nickname = "testUser";
        String password = "password123";

        //when
        userService.signupWithEmail(new UserRequestDto(role, email, nickname, password));

        //then
        User savedUser = userRepository.findByEmail(email);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getNickname()).isEqualTo(nickname);
        assertThat(savedUser.getRole().getName()).isEqualTo(role);
    }

    @Test
    @DisplayName("회원가입 실패")
    void signupFail(){
        //given
        String role = "USER";
        String email = "test@test.com";
        String nickname = "testUser";
        String password = "password123";

        userRepository.save(new User(role, email, nickname, password));

        //when, then
        Exception exception = assertThrows(IllegalArgumentException.class, ()->
                userService.signupWithEmail(new UserRequestDto(role,email,"otherUser", password))
        );
        assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess(){
        //given
        String role = "USER";
        String email = "test@test.com";
        String password = "password123";

        String encodedPassword = PasswordEncoder.encode(password);
        userRepository.save(new User(role, email, "testUser", encodedPassword));

        //when
        Authentication authentication = userService.loginUser(new LoginRequestDto(email, password));

        //then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getRole().getName()).isEqualTo(role);
    }

    @Test
    @DisplayName("로그인 실패")
    void loginFailWrongPassword(){
        //given
        String email = "test@test.com";
        userRepository.save(new User("USER", email, "testUser", "password123"));

        //when, then
        Exception exception = assertThrows(ResponseStatusException.class, ()->
                userService.loginUser(new LoginRequestDto(email,"worngPassword"))
        );
        assertThat(exception.getMessage()).contains("유효하지 않은 사용자 이름 혹은 잘못된 비밀번호");
    }
}
