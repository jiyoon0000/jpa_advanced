package com.example.demo.integration;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("아이템 생성 성공")
    void createItemSuccess() {
        // given
        User owner = userRepository.save(new User("USER", "owner@test.com", "OwnerUser", "password123"));
        User manager = userRepository.save(new User("USER", "manager@test.com", "ManagerUser", "password123"));

        // when
        itemService.createItem("Test Item", "Test Description", owner.getId(), manager.getId());

        // then
        Item savedItem = itemRepository.findAll().get(0);
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Test Item");
        assertThat(savedItem.getOwner().getEmail()).isEqualTo("owner@test.com");
        assertThat(savedItem.getManager().getEmail()).isEqualTo("manager@test.com");
    }

    @Test
    @DisplayName("아이템 생성 실패")
    void createItemFailNonexistentUser() {
        // when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem("Test Item", "Test Description", 999L, 999L)
        );
        assertThat(exception.getMessage()).isEqualTo("해당 ID에 맞는 값이 존재하지 않습니다.");
    }
}
