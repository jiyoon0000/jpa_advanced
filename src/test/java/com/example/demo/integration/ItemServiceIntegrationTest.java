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

    @Test
    @DisplayName("아이템 생성 실패 - managerId 없음")
    void createItemFailManagerId() {
        // given
        User owner = userRepository.save(new User("USER", "owner@test.com", "OwnerUser", "password123"));

        // when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem("Test Item", "Test Description", owner.getId(), 999L)
        );
        assertThat(exception.getMessage()).isEqualTo("해당 ID에 맞는 값이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("아이템 생성 실패 - ownerId 없음")
    void createItemFailInvalidOwnerId() {
        // given
        User manager = userRepository.save(new User("USER", "manager@test.com", "ManagerUser", "password123"));

        // when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem("Test Item", "Test Description", 999L, manager.getId())
        );
        assertThat(exception.getMessage()).isEqualTo("해당 ID에 맞는 값이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("아이템 생성 실패 - 필수 필드 누락")
    void createItemFail() {
        // given
        User owner = userRepository.save(new User("USER", "owner@test.com", "OwnerUser", "password123"));
        User manager = userRepository.save(new User("USER", "manager@test.com", "ManagerUser", "password123"));

        // when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem(null, "Test Description", owner.getId(), manager.getId())
        );
        assertThat(exception.getMessage()).isEqualTo("아이템 이름은 필수 입력 값입니다.");

        exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem("Test Item", null, owner.getId(), manager.getId())
        );
        assertThat(exception.getMessage()).isEqualTo("아이템 설명은 필수 입력 값입니다.");
    }

    @Test
    @DisplayName("아이템 생성 실패 - 이름 없음")
    void createItemFailBlankName() {
        // given
        User owner = userRepository.save(new User("USER", "owner@test.com", "OwnerUser", "password123"));
        User manager = userRepository.save(new User("USER", "manager@test.com", "ManagerUser", "password123"));

        // when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem(" ", "Test Description", owner.getId(), manager.getId())
        );
        assertThat(exception.getMessage()).isEqualTo("아이템 이름은 필수 입력 값입니다.");
    }


    @Test
    @DisplayName("아이템 생성 실패 - 중복된 이름")
    void createItemFailExistName() {
        // given
        User owner = userRepository.save(new User("USER", "owner@test.com", "OwnerUser", "password123"));
        User manager = userRepository.save(new User("USER", "manager@test.com", "ManagerUser", "password123"));
        itemService.createItem("Test Item", "Test Description", owner.getId(), manager.getId());

        // when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem("Test Item", "Another Description", owner.getId(), manager.getId())
        );
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 아이템 이름입니다.");
    }

}
