package com.tenco.blog.board;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    // 메인 페이지: 게시글 목록 조회
    @GetMapping("/")
    public String index(HttpServletRequest request) {

        System.out.println("=== 메인 페이지 요청 ===");

        // 1. 게시글 목록 조회
        List<Board> boardList = boardRepository.findAll();

        // 2. 연관관계 동작 확인 (개발 시 확인용)
        System.out.println("=== 목록에서 작성자 정보 확인 ===");
        for (Board board : boardList) {
            System.out.println("게시글: " + board.getTitle() +
                    " | 작성자: " + board.getUser().getUsername());
        }

        // 3. 뷰에 데이터 전달
        request.setAttribute("boardList", boardList);

        return "index";
    }

    // 게시글 상세보기 - 연관관계를 통한 작성자 정보 함께 표시
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        // 1. 게시글 조회 (User 연관관계 포함)
        Board board = boardRepository.findById(id);

        // 2. 연관관계 동작 확인 (선택사항 - 개발 시 확인용)
        System.out.println("=== 연관관계 동작 확인 ===");
        System.out.println("게시글 제목: " + board.getTitle());
        System.out.println("작성자명: " + board.getUser().getUsername());
        System.out.println("작성자 이메일: " + board.getUser().getEmail());

        // 3. 뷰에 데이터 전달
        request.setAttribute("board", board);

        return "board/detail";
    }


}

