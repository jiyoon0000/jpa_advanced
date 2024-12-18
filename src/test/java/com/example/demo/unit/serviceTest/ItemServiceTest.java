package com.example.demo.unit.serviceTest;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;

    private User owner;
    private User manager;

    @BeforeEach
    void setUp(){
        owner = new User("USER", "owner@test.com", "OwnerName", "password123");
        manager = new User("ADMIN", "manager@test.com", "ManagerName", "password123");
    }

    @Test
    @DisplayName("아이템 생성 성공")
    void createItemSuccess(){
        //given
        Long ownerId = 1L;
        Long managerId = 2L;
        String name = "Test Item";
        String description = "Test Description";

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));

        //when
        itemService.createItem(name, description, ownerId, managerId);

        //then
        verify(userRepository, times(1)).findById(ownerId);
        verify(userRepository, times(1)).findById(managerId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 생성 실패 - ownerId 사용자가 존재하지 않을 때")
    void createItemFailNoOwner(){
        //given
        Long ownerId = 1L;
        Long managerId = 2L;
        String name = "Test Item";
        String description = "Test Description";

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> itemService.createItem(name, description, ownerId, managerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID에 맞는 값이 존재하지 않습니다.");

        verify(userRepository, times(1)).findById(ownerId);
        verify(userRepository, never()).findById(managerId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 생성 실패 - managerId 사용자가 존재하지 않을 때")
    void createItemFailNoManager(){
        //given
        Long ownerId = 1L;
        Long managerId = 2L;
        String name = "Test Item";
        String description = "Test Description";

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(userRepository.findById(managerId)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> itemService.createItem(name, description, ownerId, managerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID에 맞는 값이 존재하지 않습니다.");

        verify(userRepository, times(1)).findById(ownerId);
        verify(userRepository, times(1)).findById(managerId);
        verify(itemRepository, never()).save(any(Item.class));
    }
}
