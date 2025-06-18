package com.tenco.blog.board;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

// BoardPersistRepository를 테스트 컨텍스트에 추가
@Import(BoardPersistRepository.class)
@DataJpaTest
public class BoardPersistRepositoryTest {

    @Autowired
    private BoardPersistRepository boardPersistRepository;


    // 영속성 컨텍스트를 활용한 삭제 테스트
    @Test
    public void deleteById_test(){

    }


    // 영속성 컨텍스트와 1차 캐시 동작 확인 테스트
    @Test
    @Transactional  // 같은 트랜잭션 내에서 테스트
    public void deleteById_persistence_context_test(){

    }


    @Test
    public void findById_test(){

    }

    // 1차 캐시 동작 확인 테스트
    @Test
    public void firstLevelCache_test(){

    }

    // 존재하지 않는 ID 조회 테스트
    @Test
    public void findById_not_found_test(){

    }

    @Test
    public void findAll_test(){

    }

    // 추가 테스트: JPQL 정렬 확인
    @Test
    public void findAll_order_test(){

    }


    @Test
    public void save_test(){
        // given: 테스트할 게시글 데이터 준비
        // new Board() 생성자를 통해 비영속 엔티티 생성

    }
}
