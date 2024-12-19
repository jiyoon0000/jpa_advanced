package com.example.demo.unit.entityTest;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemEntityUnitTest {

    @Test
    @DisplayName("Item 생성 시 기본 상태값 확인")
    void defaultStatusIsPending(){
        //given
        User owner = new User("USER", "owner@test.com", "OwnerName", "password123");
        User manager = new User("ADMIN", "manager@test.com", "ManagerName", "password123");

        //when
        Item item = new Item("TestItem", "TestDescription", manager, owner);

        //then
        assertThat(item.getStatus()).isNull();
        item.setDefaultStatus();
        assertThat(item.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Item 상태 업데이트")
    void updateStatus(){
        //given
        User owner = new User("USER", "owner@test.com", "OwnerName", "password123");
        User manager = new User("ADMIN", "manager@test.com", "ManagerName", "password123");
        Item item = new Item("TestItem", "TestDescription", manager, owner);

        //when
        item.setStatus("APPROVED");

        //then
        assertThat(item.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("Item의 필드 값 확인")
    void itemField(){
        //given
        User owner = new User("USER", "owner@test.com", "OwnerName", "password123");
        User manager = new User("ADMIN", "manager@test.com", "ManagerName", "password123");
        Item item = new Item("TestItem", "TestDescription", owner, manager);

        //then
        assertThat(item.getName()).isEqualTo("TestItem");
        assertThat(item.getDescription()).isEqualTo("TestDescription");

        assertThat(item.getOwner().getEmail()).isEqualTo("owner@test.com");
        assertThat(item.getOwner().getNickname()).isEqualTo("OwnerName");

        assertThat(item.getManager().getEmail()).isEqualTo("manager@test.com");
        assertThat(item.getManager().getNickname()).isEqualTo("ManagerName");
    }
}
