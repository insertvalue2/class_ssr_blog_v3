package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
    private final EntityManager em;

    // 게시글 목록 조회 - 최신순 정렬
    public List<Board> findAll() {
        // JPQL로 Board 엔티티 목록을 최신순으로 조회
        // ORDER BY b.id DESC: 최신 게시글이 위에 오도록 정렬
        String jpql = "SELECT b FROM Board b ORDER BY b.id DESC";

        Query query = em.createQuery(jpql, Board.class);
        List<Board> boardList = query.getResultList();

        System.out.println("조회된 게시글 수: " + boardList.size());
        return boardList;
    }


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