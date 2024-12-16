package com.example.demo.repository;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    default User findByIdOrThrow(Long id){
        return findById(id).orElseThrow(() -> new IllegalArgumentException("해당 User ID가 존재하지 않습니다."));
    }

    User findByEmail(String email);
}
