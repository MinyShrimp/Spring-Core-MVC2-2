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
지금부터 타임리프가 제공하는 입력 폼 기능을 적용해서 기존 프로젝트의 폼 코드를 타임리프가 지원하는 기능을 사용해서 효율적으로 개선해보자.

### FormItemController
```java
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {
  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("item", new Item());
    return "form/addForm";
  }
}
```

### addForm.html
```html
<!-- <form action="item.html" th:action method="post"> -->
<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="itemName">상품명</label>
        <!-- <input type="text" id="itemName" name="itemName" class="form-control" placeholder="이름을 입력하세요"> -->
        <input type="text" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
    </div>
    <div>
        <label for="price">가격</label>
        <!-- <input type="text" id="price" name="price" class="form-control" placeholder="가격을 입력하세요"> -->
        <input type="text" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
    </div>
    <div>
        <label for="quantity">수량</label>
        <!-- <input type="text" id="quantity" name="quantity" class="form-control" placeholder="수량을 입력하세요"> -->
        <input type="text" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
    </div>
</form>
```

### 결과
![img.png](img.png)
* `<form th:object="${item}>`
  * `<form>`에서 사용할 객체를 지정한다.
  * 하위 태그에서 `*{...}` 형식으로 지정할 수 있다.
* `th:field="*{itemName}"`
  * `*{itemName}`는 선택 변수 식을 사용했는데, `${item.itemName}`과 같다.
  * `th:field`는 `id`, `name`, `value` 속성을 자동으로 만들어준다.
    * `id`, `name`: `th:field`에서 지정한 변수 이름과 같도록 설정해준다.
    * `value`: 컨트롤러에서 넘어온 값이 있다면 여기에 넣어준다. (초기값)

## 수정 폼 처리
### FromItemController
```java
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {
  @GetMapping("/{itemId}/edit")
  public String editForm(
          @PathVariable long itemId,
          Model model
  ) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "form/editForm";
  }
}
```

### editForm.html
```html
<!-- <form action="item.html" th:action method="post"> -->
<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="id">상품 ID</label>
        <!-- <input type="text" id="id" name="id" class="form-control" value="1" th:value="${item.id}" readonly> -->
        <input type="text" th:field="*{id}" class="form-control" readonly>
    </div>
    <div>
        <label for="itemName">상품명</label>
        <!-- <input type="text" id="itemName" name="itemName" class="form-control" value="상품A" th:value="${item.itemName}"> -->
        <input type="text" th:field="*{itemName}" class="form-control">
    </div>
    <div>
        <label for="price">가격</label>
        <!-- <input type="text" id="price" name="price" class="form-control" value="10000" th:value="${item.price}"> -->
        <input type="text" th:field="*{price}" class="form-control">
    </div>
    <div>
        <label for="quantity">수량</label>
        <!-- <input type="text" id="quantity" name="quantity" class="form-control" value="10" th:value="${item.quantity}"> -->
        <input type="text" th:field="*{quantity}" class="form-control">
    </div>
</form>
```

### 결과
![img_1.png](img_1.png)

## 요구사항 추가

## 체크 박스 - 단일 1

## 체크 박스 - 단일 2

## 체크 박스 - 멀티

## 라디오 버튼

## 셀렉트 박스

## 정리
