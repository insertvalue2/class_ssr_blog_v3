package com.tenco.blog.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

// BoardRepository 테스트 컨텍스트에 추가
@Import(BoardRepository.class)
@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void findAll_test() {
        // given

        // when
        List<Board> boardList = boardRepository.findAll();
        boardList.forEach(board -> {
            System.out.println(" ----> " + board.getUser().getUsername());
        });

        // then
    }

    @Test
    public void findById_test(){
        Long id = 1L;

       //  ERGER 로딩 확인
       Board board =  boardRepository.findById(id);
       //  //  Lazy 로딩
       System.out.println(board.getUser().getUsername());
    }


}
