package com.tenco.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    // 로그인용 사용자 조회: 사용자명과 비밀번호로 검증
    public User findByUsernameAndPassword(String username, String password) {
        try {
            // JPQL로 사용자명과 비밀번호가 일치하는 사용자 조회
            String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password";

            Query query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            return (User) query.getSingleResult();

        } catch (Exception e) {
            // 일치하는 사용자가 없거나 에러 발생 시 null 반환
            // 로그인 실패를 의미함
            return null;
        }
    }


    // 회원가입: User 엔티티 영속화
    @Transactional
    public User save(User user) {
        // 비영속 상태의 User 엔티티를 영속성 컨텍스트에 저장
        // 영속성 컨텍스트가 user 객체를 관리하기 시작
        em.persist(user);

        // persist() 후 user 객체는 영속 상태가 됨
        // 트랜잭션 커밋 시점에 실제 INSERT 쿼리 실행
        // 자동 생성된 ID와 생성시간이 user 객체에 설정됨
        return user;
    }

    // 사용자명 중복 체크용 조회 메서드
    public User findByUsername(String username) {
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            return em.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            // 사용자를 찾을 수 없는 경우 null 반환
            return null;
        }
    }
}