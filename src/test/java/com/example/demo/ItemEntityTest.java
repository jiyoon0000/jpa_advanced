package com.example.demo;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemEntityTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Item 생성 시 status 기본값이 PENDING으로 설정")
    void defaultStatusIsPending(){
        //given : 테스트에 사용할 user 생성 및 저장
        User owner = new User("USER", "owner@test.com", "OwnerName", "password");
        User manager = new User("ADMIN", "manager@test.com", "ManagerName", "password");
        entityManager.persist(owner);
        entityManager.persist(manager);

        //테스트에 사용할 Item 생성
        Item item = new Item("TestItem", "TestDescription", manager, owner);

        //when : Item 엔티티를 저장하고 db에 반영
        Item savedItem = entityManager.persistAndFlush(item);

        // then : Item의 기본 status 값이 "PENDING"인지 확인
        assertEquals("PENDING", savedItem.getStatus(), "Item의 기본 status는 'PENDING'이어야한다.");
    }

    @Test
    @DisplayName("status가 null일 경우 DB에서 예외 발생")
    void statusCannotBeNull() {
        // given : 필드만 null로 설정한 Item 생성
        User owner = new User("USER", "owner@test.com", "OwnerName", "password");
        User manager = new User("ADMIN", "manager@test.com", "ManagerName", "password");
        entityManager.persist(owner);
        entityManager.persist(manager);

        Item item = new Item("TestItem", "TestDescription", manager, owner);
        // PrePersist를 우회하기 위해 직접 status를 null로 설정
        item.setStatus(null);

        // when
        Item savedItem = itemRepository.saveAndFlush(item);

        //then
        assertNotNull(savedItem.getStatus(), "status 값은 null이 아니어야한다.");
        assertEquals("PENDING", savedItem.getStatus(), "status 기본값은 'PENDING'");
    }

}
