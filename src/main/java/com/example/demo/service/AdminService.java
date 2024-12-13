package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: 4. find or save 예제 개선
    //Transactional로 인해 변경 사항 자동 저장됨
    @Transactional
    public void reportUsers(List<Long> userIds) {

        //여러 사용자를 한번에 조회
        List<User> users = userRepository.findAllById(userIds);

        //상태 변경
        for(User user : users){
            user.updateStatusToBlocked();
        }
    }
}
