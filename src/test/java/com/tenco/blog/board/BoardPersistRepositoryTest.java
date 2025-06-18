package com.tenco.blog.board;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// BoardPersistRepository를 테스트 컨텍스트에 추가
@Import(BoardPersistRepository.class)
@DataJpaTest
public class BoardPersistRepositoryTest {

    @Autowired
    private BoardPersistRepository boardPersistRepository;


    // 영속성 컨텍스트를 활용한 삭제 테스트
    @Test
    public void deleteById_test(){
        // given: 삭제할 게시글 ID 준비
        // data.sql에 의해 4개의 게시글이 존재 (id: 1, 2, 3, 4)
        Long id = 1L;

        // 삭제 전 상태 확인
        List<Board> beforeDelete = boardPersistRepository.findAll();
        int beforeCount = beforeDelete.size();

        // 삭제할 게시글이 실제로 존재하는지 확인
        Board targetBoard = boardPersistRepository.findById(id);
        Assertions.assertThat(targetBoard).isNotNull();

        System.out.println("삭제 전 게시글 수: " + beforeCount);
        System.out.println("삭제 대상 게시글: " + targetBoard.getTitle());

        // when: 영속성 컨텍스트를 통한 삭제 실행
        boardPersistRepository.deleteById(id);

        // then: 삭제 결과 검증
        List<Board> afterDelete = boardPersistRepository.findAll();
        int afterCount = afterDelete.size();

        System.out.println("삭제 후 게시글 수: " + afterCount);

        // 1. 전체 게시글 수가 1개 감소했는지 확인
        Assertions.assertThat(afterCount).isEqualTo(beforeCount - 1);
        Assertions.assertThat(afterCount).isEqualTo(3);

        // 2. 삭제된 게시글이 목록에서 제거되었는지 확인
        boolean isDeleted = afterDelete.stream()
                .noneMatch(board -> board.getId().equals(id));
        Assertions.assertThat(isDeleted).isTrue();

        // 3. 삭제된 게시글을 다시 조회하면 null이어야 함
        Board deletedBoard = boardPersistRepository.findById(id);
        Assertions.assertThat(deletedBoard).isNull();

        System.out.println("삭제 테스트 완료: 게시글이 성공적으로 삭제됨");

        // 영속성 컨텍스트의 장점 확인:
        // - 삭제 전 엔티티 존재 여부 자동 확인
        // - 1차 캐시에서 엔티티 자동 제거
        // - 트랜잭션 일관성 보장
    }


    // 영속성 컨텍스트와 1차 캐시 동작 확인 테스트
    @Test
    @Transactional  // 같은 트랜잭션 내에서 테스트
    public void deleteById_persistence_context_test(){
        // given
        Long id = 1L;

        // 1차 캐시에 엔티티 로드
        Board board1 = boardPersistRepository.findById(id);  // DB 조회 + 1차 캐시 저장
        Board board2 = boardPersistRepository.findById(id);  // 1차 캐시에서 조회

        // 동일성 확인 (1차 캐시 동작)
        Assertions.assertThat(board1).isSameAs(board2);
        System.out.println("삭제 전 1차 캐시 동작 확인: " + (board1 == board2));

        // when: 삭제 실행
        boardPersistRepository.deleteById(id);

        // then: 1차 캐시에서도 제거되었는지 확인
        Board deletedBoard = boardPersistRepository.findById(id);
        Assertions.assertThat(deletedBoard).isNull();

        System.out.println("삭제 후 1차 캐시에서도 제거 확인 완료");

        // 영속성 컨텍스트의 일관성:
        // - em.remove() 실행 시 1차 캐시에서도 엔티티 제거
        // - 같은 트랜잭션 내에서 일관된 상태 유지
    }


    @Test
    public void findById_test(){
        // given: 조회할 게시글 ID

        // Board 엔티티에 id 데이터 타입은 Integer로 되어 있음 - 오류 발생(수정 간편함 확인하기)
        Long id = 1L;
        //Integer id = 1;

        // when: 기본키로 게시글 조회
        Board board = boardPersistRepository.findById(id);

        // then: 조회 결과 검증
        System.out.println("findById_test/board : " + board);

        // 조회된 게시글이 null이 아닌지 확인
        Assertions.assertThat(board).isNotNull();

        // 조회된 게시글의 ID가 요청한 ID와 일치하는지 확인
        Assertions.assertThat(board.getId()).isEqualTo(id);

        // 필수 필드들이 제대로 조회되었는지 확인
        Assertions.assertThat(board.getTitle()).isNotEmpty();
        Assertions.assertThat(board.getContent()).isNotEmpty();
        Assertions.assertThat(board.getUsername()).isNotEmpty();
        Assertions.assertThat(board.getCreatedAt()).isNotNull();

        System.out.println("조회된 게시글 제목: " + board.getTitle());
        System.out.println("조회된 게시글 작성자: " + board.getUsername());
    }

    // 1차 캐시 동작 확인 테스트
    @Test
    public void firstLevelCache_test(){
        // given
        Long id = 1L;

        // when: 같은 ID로 두 번 조회
        Board board1 = boardPersistRepository.findById(id);  // DB에서 조회 + 1차 캐시 저장
        Board board2 = boardPersistRepository.findById(id);  // 1차 캐시에서 조회 (DB 접근 없음)

        // then: 동일성 확인 (같은 인스턴스여야 함)
        Assertions.assertThat(board1).isSameAs(board2);  // == 비교 (참조 비교)
        Assertions.assertThat(board1 == board2).isTrue(); // 1차 캐시 덕분에 같은 객체

        System.out.println("1차 캐시 동작 확인: board1 == board2 = " + (board1 == board2));
        System.out.println("첫 번째 조회: " + System.identityHashCode(board1));
        System.out.println("두 번째 조회: " + System.identityHashCode(board2));

        // 1차 캐시의 장점:
        // 1. 성능 향상 (DB 접근 횟수 감소)
        // 2. 동일성 보장 (같은 엔티티는 같은 인스턴스)
        // 3. 일관성 유지 (트랜잭션 내에서 동일한 상태 보장)
    }

    // 존재하지 않는 ID 조회 테스트
    @Test
    public void findById_not_found_test(){
        // given: 존재하지 않는 ID
        Long nonExistentId = 999L;

        // when: 존재하지 않는 게시글 조회
        Board board = boardPersistRepository.findById(nonExistentId);

        // then: null 반환 확인
        Assertions.assertThat(board).isNull();

        System.out.println("존재하지 않는 ID 조회 결과: " + board);

        // find() 메서드의 특징: 결과가 없어도 예외 발생 안함 (null 반환)
        // 이는 getSingleResult()와 다른 점 (NoResultException 발생)
    }

    @Test
    public void findAll_test(){
        // given: 테스트 데이터 준비
        // data.sql에 의해 4개의 더미 데이터가 자동으로 준비됨
        // H2 데이터베이스가 테스트용으로 자동 설정됨

        // when: JPQL을 통한 목록 조회 실행
        List<Board> boardList = boardPersistRepository.findAll();

        // then: 조회 결과 검증
        System.out.println("findAll_test/size : " + boardList.size());
        System.out.println("findAll_test/첫번째 게시글 제목 : " + boardList.get(0).getTitle());

        // 조회된 게시글 수 검증
        Assertions.assertThat(boardList.size()).isEqualTo(4);

        // ORDER BY createdAt DESC로 정렬되므로 최신 글이 첫 번째
        // 실제로는 data.sql의 삽입 순서에 따라 결과가 달라질 수 있음
        Assertions.assertThat(boardList.get(0)).isNotNull();

        // 모든 게시글이 영속 상태인지 확인
        for (Board board : boardList) {
            Assertions.assertThat(board.getId()).isNotNull();
            Assertions.assertThat(board.getTitle()).isNotEmpty();
            Assertions.assertThat(board.getCreatedAt()).isNotNull();
        }

        // JPQL의 장점: 객체 지향적 접근
        // board.getTitle() 등 엔티티 메서드 직접 사용 가능
        System.out.println("JPQL 조회 성공 - 영속성 컨텍스트에서 관리되는 엔티티들");
    }

    // 추가 테스트: JPQL 정렬 확인
    @Test
    public void findAll_order_test(){
        // given

        // when
        List<Board> boardList = boardPersistRepository.findAll();

        // then: 생성 시간 기준 내림차순 정렬 확인
        if (boardList.size() >= 2) {
            Board firstBoard = boardList.get(0);
            Board secondBoard = boardList.get(1);

            // 첫 번째 게시글의 생성시간이 두 번째보다 최신이어야 함
            Assertions.assertThat(firstBoard.getCreatedAt())
                    .isAfterOrEqualTo(secondBoard.getCreatedAt());
        }
    }


    @Test
    public void save_test(){
        // given: 테스트할 게시글 데이터 준비
        // new Board() 생성자를 통해 비영속 엔티티 생성
        Board board = new Board("제목5", "내용5", "ssar");

        // 저장 전 상태 확인: id는 null이어야 함
        Assertions.assertThat(board.getId()).isNull();
        System.out.println("저장 전 board : " + board);

        // when: 영속성 컨텍스트를 통한 엔티티 저장
        Board savedBoard = boardPersistRepository.save(board);

        // then: 저장 결과 검증
        // 1. 저장 후 자동 생성된 ID 확인
        Assertions.assertThat(savedBoard.getId()).isNotNull();
        Assertions.assertThat(savedBoard.getId()).isGreaterThan(0);

        // 2. 입력한 데이터가 올바르게 저장되었는지 확인
        Assertions.assertThat(savedBoard.getTitle()).isEqualTo("제목5");
        Assertions.assertThat(savedBoard.getContent()).isEqualTo("내용5");
        Assertions.assertThat(savedBoard.getUsername()).isEqualTo("ssar");

        // 3. 자동으로 생성된 생성시간 확인
        Assertions.assertThat(savedBoard.getCreatedAt()).isNotNull();

        System.out.println("저장 후 board : " + savedBoard);

        // 4. 원본 객체와 반환된 객체가 동일한 참조인지 확인
        // 영속성 컨텍스트는 같은 엔티티에 대해 같은 인스턴스를 보장
        Assertions.assertThat(board).isSameAs(savedBoard);
    }
}
