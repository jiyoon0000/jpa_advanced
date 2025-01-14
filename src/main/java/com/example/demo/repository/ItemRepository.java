package com.example.demo.repository;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    default Item findByIdOrThrow(Long id){
        return findById(id).orElseThrow(() -> new IllegalArgumentException("해당 Item ID가 존재하지 않습니다."));
    }

    boolean existsByName(String name);
}
