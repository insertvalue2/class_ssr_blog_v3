package com.tenco.blog.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 사용자명 중복 방지를 위한 유니크 제약조건
    @Column(unique = true)
    private String username;

    private String password;
    private String email;

    // 엔티티가 영속화될 때 자동으로 현재 시간이 설정됨
    @CreationTimestamp
    private Timestamp createdAt;

    // 빌더 패턴: 객체 생성 시 가독성과 안전성 향상
    @Builder
    public User(Integer id, String username, String password, String email, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
    }
}