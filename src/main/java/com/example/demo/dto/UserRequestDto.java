package com.example.demo.dto;

import com.example.demo.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    @NotBlank
    private String role;

    public UserRequestDto(String role, String email, String nickname, String password) {
        this.role = role;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public User toEntity() {
        return new User(
                this.role,
                this.email,
                this.nickname,
                this.password
        );
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }
}