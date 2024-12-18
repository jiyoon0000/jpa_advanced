package com.example.demo.unit.entityTest;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    @DisplayName("User entity 생성자")
    void testUserConstructor(){
        //given
        String role = "USER";
        String email = "test@test.com";
        String nickname = "testNickname";
        String password = "password123";

        //when
        User user = new User(role, email, nickname, password);

        //then
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getStatus()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("User 상태 변경 - BLOCKED")
    void testUpdateStatusToBlocked(){
        //given
        User user = new User("USER", "test@test.com", "testNickname", "password123");

        //when
        user.updateStatusToBlocked();

        //then
        assertThat(user.getStatus()).isEqualTo("BLOCKED");
    }

    @Test
    @DisplayName("User Getter 테스트")
    void testUserGetter(){
        //given
        User user = new User("ADMIN", "admin@test.com", "adminUser", "adminpassword");

        //when, then
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getEmail()).isEqualTo("admin@test.com");
        assertThat(user.getNickname()).isEqualTo("adminUser");
        assertThat(user.getPassword()).isEqualTo("adminpassword");
        assertThat(user.getStatus()).isEqualTo("NORMAL");
    }
}
