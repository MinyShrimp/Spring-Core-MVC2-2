# 메시지, 국제화
## 프로젝트 설정

## 메시지, 국제화 소개
### 메시지
화면에 보이는 문구가 마음에 들지 않는다고, "**상품명**"이라는 단어를 모두 "**상품 이름**"으로 고쳐달라고 하면 어떻게 해야할까?
여러 화면에 보이는 상품명, 가격, 수량 등, `label`에 있는 단어를 변경하려면 다 찾아가면서 모두 변경해야 한다.
지금처럼 화면 수가 적으면 문제가 되지 않지만 화면이 수십개 이상이라면 문제가 된다.

이런 다양한 메시지를 한 곳에서 관리하도록 하는 기능을 메시지 기능이라 한다.

예를 들어서 `messages.propeties`라는 메시지 관리용 파일을 만들고,
```properties
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```
각 HTML들은 다음과 같이 해당 데이터를 key값으로 불러서 사용하는 것이다.

```html
<label for="itemName" th:text="#{item.itemName}"></label>
```

### 국제화
위에서 설명한 기능에서 추가하여, `messages.properties`를 각 나라별로 만들어서 관리하면 서비스를 국제화 할 수 있다.

`messages_en.properties`
```properties
item=Item
item.id=Item ID
item.itemName=Item Name
item.price=price
item.quantity=quantity
```

`messages_ko.properties`
```properties
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```

어느 국가에서 접근한 것인지 인식하는 방법은 2가지 방법이 있다.
* HTTP Accept-Language Header
* 사용자가 직접 언어를 선택하여 쿠키에 남긴다.

메시지와 국제화 기능을 직접 구현할 수도 있겠지만, 스프링은 기본적인 메시지와 국제화 기능을 모두 제공한다.
그리고 타임리프도 스프링이 제공하는 메시지와 국제화 기능을 편리하게 통합해서 제공한다.

## 스프링 메시지 소스 설정

## 스프링 메시지 소스 사용

## 웹 애플리케이션에 메시지 적용하기

## 웹 애플리케이션에 국제화 적용하기
