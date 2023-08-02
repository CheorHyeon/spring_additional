<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<h3 align="center">점프 투 스프링부트 - SBB (Jump To SpringBoot - SBB)</h3>

  <p align="center">
    점프 투 스프링부트 교재의 SBB 추가 기능을 구현한 프로젝트 입니다.
    <br />
  </p>

<br>

<!-- ABOUT THE PROJECT -->

## 프로젝트 개요

<div hidden id="123"></div>
<!-- [![Product Name Screen Shot][product-screenshot]](https://example.com) -->

- [점프 투 스프링부트](https://wikidocs.net/book/7601) - 점프 투 스프링부트 클론 코딩 및 `3-15 SBB 추가기능`을 구현한 프로젝트 입니다.
- 해당 교재는 스프링부트를 활용하여 게시판을 만드는 교재이며, 교재 내용을 학습하고 추가 기능으로 남겨둔 내용들을 구현한 프로젝트 입니다.
- 구현이 완료된 이후에는 교재의 나머지 부분(4장) 또한 학습하고, 배포를 진행 할 예정입니다.

<br>

## 기능 소개

- 기존
  - 회원가입
  - 질문 작성 / 조회(페이징) / 수정 / 삭제 / 추천
  - 답변 작성 / 조회 / 수정 / 삭제 / 추천
  - 질문 검색 기능(질문 작성자, 내용, 제목, 답변 내용, 답변 작성자)

- 추가 기능 [점프 투 스프링부트 3-15 SBB 추가 기능](https://wikidocs.net/162833)
  - [x] 조회수
    - 로컬 스토리지와 Ajax 활용(브라우저 당 조회수 1회만 증가)
    - [PR 바로가기](https://github.com/CheorHyeon/spring_additional/pull/4)
  - [x] 프로필
    - 사용자에 대한 기본정보
    - 작성한 질문, 답변 확인 가능
    - [Issue 바로가기](https://github.com/CheorHyeon/spring_additional/issues/5)
  - [x] 최근 답변, 최근 댓글
    - 최근 답변 조회
    - 최근 댓글은 미구현
    - [PR 바로가기](https://github.com/CheorHyeon/spring_additional/pull/7)
  - [x] 카테고리
    - 게시판 3개 구분(질문과답변, 자유게시판, 버그및건의)
    - [PR 바로가기](https://github.com/CheorHyeon/spring_additional/pull/9)
  - [x] 마크다운 에디터
    - 마크다운 에디터(simpleMDE) 적용
    - [PR 바로가기](https://github.com/CheorHyeon/spring_additional/pull/11)
  - [x] 비밀번호 찾기와 변경
    - 임시 비밀번호 이메일 발송(비밀번호 찾기)
    - 비밀번호 변경
    - [PR 바로가기](https://github.com/CheorHyeon/spring_additional/pull/13)
  - [ ] 소셜 로그인(카카오, 구글)
  - [ ] 답변 페이징, 정렬
  - [ ] 댓글

<br>

## 프로젝트 결과물
- [벨로그 기능별 정리](https://velog.io/@puar12?tag=%EC%A0%90%ED%94%84%ED%88%AC%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)
- [배포 사이트(예정)]()

<!-- 템플릿 출처 : https://github.com/othneildrew/Best-README-Template -->
