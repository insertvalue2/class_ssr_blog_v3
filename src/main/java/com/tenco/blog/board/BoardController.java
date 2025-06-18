package com.tenco.blog.board;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BoardController {

    // 의존성 주입: 스프링이 BoardNativeRepository 객체를 자동으로 주입
    // private final BoardNativeRepository boardNativeRepository;

    // V2에서는 PersistRepository 사용
    @Autowired // DI
    private BoardPersistRepository boardPersistRepository;

    // 게시글 수정 폼 페이지
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable Long id, HttpServletRequest request) {
        // 수정할 게시글을 영속 상태로 조회
        Board board = boardPersistRepository.findById(id);

        if (board == null) {
            throw new RuntimeException("수정할 게시글을 찾을 수 없습니다. ID: " + id);
        }

        // 기존 데이터를 수정 폼에 미리 채워넣기 위해 뷰에 전달
        request.setAttribute("board", board);

        return "board/update-form";
    }

    // 게시글 수정 처리: Dirty Checking 활용
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Long id, BoardRequest.UpdateDTO reqDTO) {
        // @PathVariable Long id: URL 경로의 {id} 값
        // BoardRequest.UpdateDTO reqDTO: 폼에서 전송된 수정 데이터

        try {
            // Dirty Checking을 활용한 수정 실행
            boardPersistRepository.updateById(id, reqDTO);

            // 수정 완료 후 해당 게시글 상세보기 페이지로 리다이렉트
            // PRG 패턴 적용으로 중복 수정 방지
            return "redirect:/board/" + id;

        } catch (IllegalArgumentException e) {
            // 수정할 게시글이 존재하지 않거나 유효성 검증 실패
            throw new RuntimeException("게시글 수정 실패: " + e.getMessage());
        }

        // Dirty Checking의 장점:
        // 1. UPDATE 쿼리 자동 생성
        // 2. 변경된 필드만 업데이트 (성능 최적화)
        // 3. 영속성 컨텍스트 일관성 유지
        // 4. 1차 캐시 자동 갱신
    }


    // 삭제는 @DeleteMapping 이지만 form 태그를 활용 중 ( 대안 - 자바스트립트 fetch 함수 활용)
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            boardPersistRepository.deleteById(id);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            // 사용자 친화적인 에러 메시지와 함께 적절한 페이지로 리다이렉트
            return "redirect:/?error=notfound";
        }
    }


    // 게시글 상세보기: 1차 캐시를 활용한 효율적 조회
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, HttpServletRequest request) {
        // @PathVariable Long id: URL 경로의 {id} 값을 Long 타입으로 자동 변환
        // 예: /board/1 → id = 1L

        // EntityManager의 find() 메서드로 최적화된 조회
        Board board = boardPersistRepository.findById(id);

        // 게시글이 존재하지 않는 경우 처리
        if (board == null) {
            // 실무에서는 404 에러 페이지로 리다이렉트하거나 예외 처리
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }

        // 조회된 게시글을 뷰에 전달
        request.setAttribute("board", board);

        // 1차 캐시 효과:
        // 만약 이 요청 처리 중에 같은 게시글을 다시 조회한다면
        // DB에 접근하지 않고 1차 캐시에서 바로 가져옴

        return "board/detail";
    }


    // 메인 페이지: 게시글 목록 조회
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        // JPQL을 통한 게시글 목록 조회
        // 영속성 컨텍스트에서 관리되는 엔티티들을 반환
        List<Board> boardList = boardPersistRepository.findAll();

        // 뷰에 데이터 전달
        // "boardList"라는 이름으로 템플릿에서 사용 가능
        request.setAttribute("boardList", boardList);

        // index.html 템플릿 렌더링
        return "index";
    }


    @GetMapping("/board/save-form")
    public String saveForm() {
        // 게시글 작성 폼을 보여주는 뷰 반환
        // templates/board/save-form.html 파일을 렌더링
        return "board/save-form";
    }

    // 참고 사항
    // 1. DispatcherServlet이 요청 수신
    // 2. HandlerMapping이 적절한 Controller 메서드 찾기
    // 3. HandlerAdapter가 메서드 파라미터 분석
    // .....
    // 4. ArgumentResolver 동작

    // 게시글 저장: DTO 패턴과 영속성 컨텍스트 활용
    @PostMapping("/board/save")
    // Spring이 폼 데이터를 객체로 변환하는 과정 (Spring의 데이터 바인딩 메커니즘)
    // 폼 데이터 바인딩: Spring이 HTTP 요청 파라미터를 객체로 자동 변환
    // HandlerMethodArgumentResolver가 객체 생성 및 프로퍼티 설정 담당
    public String save(BoardRequest.SaveDTO reqDTO){
        // HTTP 요청: title=값&content=값&username=값 (application/x-www-form-urlencoded)
        // Spring 처리: new SaveDTO() 생성 후 setter 메서드들 자동 호출

        // 1. DTO에서 Entity로 변환
        //    - 계층 간 데이터 전송을 위한 DTO 사용
        //    - toEntity() 메서드로 명확한 변환 로직
        Board board = reqDTO.toEntity();

        // 2. 영속성 컨텍스트를 통한 엔티티 저장
        //    - V1: 직접 SQL 작성 및 실행
        //    - V2: JPA EntityManager의 persist() 사용
        Board savedBoard = boardPersistRepository.save(board);

        // 3. 저장된 엔티티는 영속 상태로 관리됨
        //    - 자동 생성된 ID와 생성시간 포함
        //    - 영속성 컨텍스트에서 변경 감지(Dirty Checking) 적용

        // 4. PRG 패턴으로 메인 페이지로 리다이렉트
        return "redirect:/";
    }



}

