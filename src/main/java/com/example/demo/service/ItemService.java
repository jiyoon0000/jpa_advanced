package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createItem(String name, String description, Long ownerId, Long managerId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("아이템 이름은 필수 입력 값입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("아이템 설명은 필수 입력 값입니다.");
        }
        if (itemRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 아이템 이름입니다.");
        }

        User owner = userRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        User manager = userRepository.findById(managerId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));

        Item item = new Item(name, description, owner, manager);
        itemRepository.save(item);
    }
}
