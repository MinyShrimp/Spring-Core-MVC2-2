# 검증 1 - Validation

## 검증 요구사항

### 요구사항: 검증 로직 추가

* 타입 검증
    * 가격, 수량에 문자가 들어가면 검증 오류 처리
* 필드 검증
    * 상품명: 필수, 공백 X
    * 가격: 10000원 이상, 1백만원 이하
    * 수량: 최대 9999
* 특정 필드의 범위를 넘어서는 검증
    * 가격 * 수량의 합은 10000원 이상

### 중요성

컨트롤러의 중요한 역할 중 하나는 HTTP 요청이 정상인지 검증하는 것이다.

> 참고: 클라이언트 검증, 서버 검증
> * 클라이언트 검증은 조작하기 매우 쉽다.
> * 서버만 검증하면, 즉각적인 고객 사용성이 부족해진다.
> * 줄을 적절히 섞어서 사용하되, 최종적으로 서버 검증은 필수
> * API 방식을 사용하면 API 스펙을 잘 정의해서 검증 오류를 API 응답 결과에 잘 남겨주어야 함

## 프로젝트 준비 V1

## 검증 직접 처리 - 소개

### 성공 시나리오

![img_1.png](img_1.png)

### 실패 시나리오

![img.png](img.png)

## 검증 직접 처리 - 개발

### ValidationItemController V1

```java
@PostMapping("/add")
public String addItem(
        @ModelAttribute Item item,
        RedirectAttributes redirectAttributes,
        Model model
) {
    // 검증 오류 결과를 보관
    Map<String, String> errors = new HashMap<>();

    // 검증 로직
    if (!StringUtils.hasText(item.getItemName())) {
        errors.put("itemName", "상품 이름은 필수입니다.");
    }

    if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
        errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
    }

    if (item.getQuantity() == null || item.getQuantity() >= 9999) {
        errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
    }

    // 특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if (!errors.isEmpty()) {
        model.addAttribute("errors", errors);
        return "validation/v1/addForm";
    }

    // 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v1/items/{itemId}";
}
```

### addForm.html

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link href="../../css/bootstrap.min.css"
          rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
    <link href="../../css/main.css" rel="stylesheet" th:href="@{/css/main.css}">
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>[[#{page.addItem}]]</h2>
    </div>

    <form action="item.html" method="post" th:action th:object="${item}">
        <div th:if="${errors?.containsKey('globalError')}">
            <p class="field-error" th:text="${errors['globalError']}"></p>
        </div>

        <div>
            <label for="itemName">[[#{label.item.itemName}]]</label>
            <!-- <input class="form-control" th:field="*{itemName}" th:placeholder="#{input.placeholder.name}" type="text"> -->
            <input th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"
                   th:field="*{itemName}"
                   th:placeholder="#{input.placeholder.name}"
                   type="text">
            <div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}"></div>
        </div>
        <div>
            <label for="price">[[#{label.item.price}]]</label>
            <!-- <input class="form-control" th:field="*{price}" th:placeholder="#{input.placeholder.price}" type="text"> -->
            <input th:class="${errors?.containsKey('price')} ? 'form-control field-error' : 'form-control'"
                   th:field="*{price}"
                   th:placeholder="#{input.placeholder.price}"
                   type="text">
            <div class="field-error" th:if="${errors?.containsKey('price')}" th:text="${errors['price']}"></div>
        </div>
        <div>
            <label for="quantity">[[#{label.item.quantity}]]</label>
            <!-- <input class="form-control"
                   th:field="*{quantity}"
                   th:placeholder="#{input.placeholder.quantity}"
                   type="text"> -->
            <input th:class="${errors?.containsKey('quantity')} ? 'form-control field-error' : 'form-control'"
                   th:field="*{quantity}"
                   th:placeholder="#{input.placeholder.quantity}"
                   type="text">
            <div class="field-error" th:if="${errors?.containsKey('quantity')}" th:text="${errors['quantity']}"></div>
        </div>

        <hr class="my-4">

        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit">[[#{button.save}]]</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/validation/v1/items}'|"
                        type="button">[[#{button.cancel}]]
                </button>
            </div>
        </div>
    </form>
</div> <!-- /container -->
</body>
</html>
```

### 결과

![img_2.png](img_2.png)

### 문제점

* 뷰 템플릿에서 중복 코드가 너무 많다.
* 위의 코드를 추가 페이지만이 아닌, 수정페이지도 해줘야 한다.
* 타입 오류 처리가 안된다.
    * 이 오류는 스프링 MVC에서 컨트롤러에 진입하기도 전에 터지는 에러이기 떄문에 지금 방법으로는 처리할 수 없다.

## 프로젝트 준비 V2

## BindingResult 1

## BindingResult 2

## FieldError, ObjectError

## 오류 코드와 메시지 처리 1

## 오류 코드와 메시지 처리 2

## 오류 코드와 메시지 처리 3

## 오류 코드와 메시지 처리 4

## 오류 코드와 메시지 처리 5

## 오류 코드와 메시지 처리 6

## Validator 분리 1

## Validator 분리 2
