package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Long managerId;

    @NotNull
    private Long ownerId;

}
