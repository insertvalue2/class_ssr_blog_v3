package com.tenco.blog.board;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

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

