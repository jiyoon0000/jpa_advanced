package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
@NoArgsConstructor
@DynamicInsert //DynamicInsert를 사용하여 null인 필드는 제외되고 DB의 기본값이 적용됨
// TODO: 6. Dynamic Insert
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    //status가 null일 경우, INSERT SQL에서 제외되고, DB 기본값 PENDING이 적용
    @Column(nullable = false)
    private String status;

    @PrePersist
    public void setDefaultStatus(){
        if(this.status == null){
            this.status = "PENDING";
        }
    }

    public Item(String name, String description, User manager, User owner) {
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.owner = owner;
    }

    public void setStatus(String status){
        this.status = status;
    }

}
