# 타임리프 - 스프링 통합과 폼
## 프로젝트 설정
[MVC 1편 - 7강](https://github.com/MinyShrimp/Spring-Core-MVC-Seven)에서 했던 프로젝트를 바탕으로 진행된다. 

## 타임리프 스프링 통합
* [기본 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
* [스프링 통합 메뉴얼](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)

타임리프는 스프링 없이도 동작하지만, 스프링과 통합을 위한 다양한 기능을 제공한다.

### 스프링 통합으로 추가되는 기능들
* 스프링의 SpringEL 문법 통합
* `${@myBean.doSomething()}`처럼 스프링 빈 호출 지원
* 편리한 폼 관리를 위한 추가 속성
  * `th:object`: 기능 강화, 폼 커맨드 객체 선택
  * `th:field`, `th:errors`, `th:errorclass`
* 폼 컴포넌트 기능
  * checkbox, radio button, List 등을 편리하게 사용할 수 있는 기능 지원
* 스프링의 메시지, 국제화 기능의 편리한 통합
* 스프링의 검증, 오류 처리 통합
* 스프링의 변환 서비스 통합

### 타임리프 관련 설정 변경
* `application.properties`
* [링크](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.templating)

## 입력 폼 처리

## 요구사항 추가

## 체크 박스 - 단일 1

## 체크 박스 - 단일 2

## 체크 박스 - 멀티

## 라디오 버튼

## 셀렉트 박스

## 정리
