package com.example.demo.unit.serviceTest;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp(){
        userA = new User("USER", "userA@test.com", "UserA", "password123");
        userB = new User("USER", "userB@test.com", "UserB", "password123");
    }

    @Test
    @DisplayName("사용자 상태를 BLOCKED로 변경")
    void reportUserSuccess(){
        //given
        List<Long> userIds = Arrays.asList(1L,2L);
        List<User> users = Arrays.asList(userA, userB);

        when(userRepository.findAllById(userIds)).thenReturn(users);

        //when
        adminService.reportUsers(userIds);

        //then
        assertThat(userA.getStatus()).isEqualTo("BLOCKED");
        assertThat(userB.getStatus()).isEqualTo("BLOCKED");

        verify(userRepository,times(1)).findAllById(userIds);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("존재하지 않는 사용자")
    void reportUsersEmpty(){
        //given
        List<Long> userIds = Arrays.asList(100L, 101L);

        when(userRepository.findAllById(userIds)).thenReturn(List.of());

        //when
        adminService.reportUsers(userIds);

        //then
        verify(userRepository, times(1)).findAllById(userIds);
        verifyNoMoreInteractions(userRepository);
    }
}
