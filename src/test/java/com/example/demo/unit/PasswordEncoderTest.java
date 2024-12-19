package com.example.demo.unit;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.demo.util.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    @Test
    @DisplayName("비밀번호 암호화")
    void passwordEncoding(){
        //given : 테스트에 사용할 원본 비밀번호 설정
        String rawPassword = "testPassword123";

        //when : BCrypt를 사용하여 비밀번호 암호화
        //hashToString(int cost, char[] password) : 비밀번호를 BCrypt로 암호화하여 문자열 형식으로 반환
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        //then : 암호화됨 비밀번호가 null이 아님을 확인한 후, 원본 비밀번호와 다름을 확인
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("비밀번호와 암호화된 비밀번호 일치")
    void passwordMatch(){
        //given : 원본 비밀번호와 암호화된 비밀번호 생성
        String rawPassword = "testPassword123";
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        //when
        boolean matches = PasswordEncoder.matches(rawPassword,encodedPassword);

        //then : 비밀번호가 올바르게 검증되었는지 확인
        assertTrue(matches);
    }

    @Test
    @DisplayName("잘못된 비밀번호는 암호화된 비밀번호와 일치하지 않음")
    void passwordNotMatch(){
        //given : 원본 비밀번호와 암호화된 비밀번호 생성
        String rawPassword = "testPassword123";
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        //when : BCrypt를 사용하여 원본 비밀번호가 암호화된 비밀번호와 일치하는지 검증
        boolean matches = PasswordEncoder.matches("wrongPassword",encodedPassword);

        //then : 잘못된 비밀번호가 검증에 실패했는지 확인
        assertFalse(matches);
    }

}
