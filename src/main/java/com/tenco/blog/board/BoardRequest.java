package com.tenco.blog.board;


import lombok.Data;

// 요청 데이터를 담는 DTO 클래스
// 컨트롤러와 비즈니스 로직 사이의 데이터 전송 객체
public class BoardRequest {

    // 정적 내부 클래스로 기능별 DTO 분리
    // SaveDTO: 게시글 저장 요청 데이터
    @Data
    public static class SaveDTO {
        private String title;
        private String content;
        private String username;

        // DTO에서 Entity로 변환하는 메서드
        // 계층 간 데이터 변환을 명확하게 분리
        // 비즈니스 로직(Entity 생성)을 DTO에서 캡슐화
        // TODO - 오류 나는 부분 추후 수정
//        public Board toEntity(){
//            return new Board(title, content, username);
//        }
    }

    // 게시글 수정용 DTO 추가
    @Data
    public static class UpdateDTO {
        private String title;
        private String content;
        private String username;

        // UpdateDTO는 새로운 엔티티를 생성하지 않음
        // 기존 영속 엔티티의 값을 변경하는 용도로만 사용

        // 검증 메서드 (선택사항)
        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }
    }
}
