package com.example.demo.controller;

import com.example.demo.dto.ItemRequestDto;
import com.example.demo.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<String> createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
         itemService.createItem(itemRequestDto.getName(),
                                itemRequestDto.getDescription(),
                                itemRequestDto.getOwnerId(),
                                itemRequestDto.getManagerId());
         return ResponseEntity.ok("아이템이 성공적으로 생성되었습니다.");
    }
}
