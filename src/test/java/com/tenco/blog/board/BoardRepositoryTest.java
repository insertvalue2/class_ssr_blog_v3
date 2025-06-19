package com.tenco.blog.board;

import com.tenco.blog.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(BoardRepository.class)
@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void save_연관관계_포함_게시글_저장_테스트() {
        // given: User 객체 생성 (실제로는 세션에서 가져온 사용자)
        User user = User.builder()
                .id(1L)  // 기존 사용자 ID
                .username("testuser")
                .email("test@email.com")
                .build();

        // Board 엔티티 생성 (User와 연관관계 설정)
        Board board = Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용입니다")
                .user(user)  // 연관관계 설정
                .build();

        // 저장 전 상태 확인
        Assertions.assertThat(board.getId()).isNull();
        System.out.println("저장 전 Board: " + board.getTitle());
        System.out.println("작성자: " + board.getUser().getUsername());

        // when: 게시글 저장 (연관관계 포함)
        Board savedBoard = boardRepository.save(board);

        // then: 저장 결과 검증
        // 1. 자동 생성된 ID 확인
        Assertions.assertThat(savedBoard.getId()).isNotNull();
        Assertions.assertThat(savedBoard.getId()).isGreaterThan(0);

        // 2. 입력한 데이터가 올바르게 저장되었는지 확인
        Assertions.assertThat(savedBoard.getTitle()).isEqualTo("테스트 게시글");
        Assertions.assertThat(savedBoard.getContent()).isEqualTo("테스트 내용입니다");

        // 3. 연관관계가 올바르게 저장되었는지 확인
        Assertions.assertThat(savedBoard.getUser()).isNotNull();
        Assertions.assertThat(savedBoard.getUser().getUsername()).isEqualTo("testuser");

        // 4. 자동으로 생성된 생성시간 확인
        Assertions.assertThat(savedBoard.getCreatedAt()).isNotNull();

        System.out.println("저장 후 Board ID: " + savedBoard.getId());
        System.out.println("연관된 User: " + savedBoard.getUser().getUsername());

        // 5. 원본 객체와 반환된 객체가 동일한 참조인지 확인
        Assertions.assertThat(board).isSameAs(savedBoard);
    }

}