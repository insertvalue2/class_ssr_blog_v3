package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
    private final EntityManager em;

    // 게시글 단건 조회 - 연관관계 포함
    public Board findById(Long id) {
        // EntityManager의 find() 메서드로 기본키 조회
        Board board = em.find(Board.class, id);

        // 조회 결과 검증
        if (board == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }

        return board;
        // 반환된 Board 객체는 연관된 User 정보에도 접근 가능
    }
}