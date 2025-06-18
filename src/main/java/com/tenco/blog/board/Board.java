package com.tenco.blog.board;

import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// @NoArgsConstructor: JPA에서 엔티티는 기본 생성자가 필요
// JPA가 리플렉션을 통해 객체를 생성할 때 사용
@NoArgsConstructor
@Data
@Table(name = "board_tb")
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String username;

    // @CreationTimestamp: Hibernate가 제공하는 어노테이션
    // 엔티티가 처음 저장될 때 현재 시간을 자동으로 설정
    // V1에서는 SQL에서 now()를 직접 사용했지만, V2에서는 JPA가 자동 처리
    // pc -> db (날짜주입)
    @CreationTimestamp
    private Timestamp createdAt;

    // 비즈니스 로직을 위한 생성자
    // id와 createdAt은 JPA가 자동으로 설정하므로 매개변수에서 제외
    public Board(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
        // id와 createdAt은 JPA/Hibernate가 자동으로 설정
    }

    public String getTime(){
        return MyDateUtil.timestampFormat(createdAt);
    }

    // 영속 엔티티 수정을 위한 비즈니스 메서드
    public void update(BoardRequest.UpdateDTO updateDTO) {
        // 비즈니스 규칙 검증
        updateDTO.validate();

        // 영속 상태 엔티티의 필드 값 변경
        // 이 변경사항들이 Dirty Checking 대상이 됨
        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();
        this.username = updateDTO.getUsername();

        // 변경 감지(Dirty Checking) 동작 과정:
        // 1. 영속성 컨텍스트가 엔티티 최초 상태를 스냅샷으로 보관
        // 2. 필드 값 변경 시 현재 상태와 스냅샷 비교
        // 3. 트랜잭션 커밋 시점에 변경된 필드만 UPDATE 쿼리 자동 생성
        // 4. UPDATE board_tb SET title=?, content=?, username=? WHERE id=?
    }

    // 개별 필드 수정 메서드 (필요시 사용)
    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다");
        }
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입니다");
        }
        this.content = newContent;
    }
}