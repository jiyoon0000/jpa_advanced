package com.example.demo;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ItemEntityTest {

    @Autowired
    private TestEntityManager entityManager;

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

}
