# 검증 2 - Bean Validation

## 소개

### 저번 시간에서

검증 기능을 저번 시간처럼 매번 코드로 작성하는 것은 상당히 번거롭다.
특히 특정 필드에 대한 검증 로직은 대부분 빈 값인지 아닌지, 특정 크기를 넘는지 아닌지와 같이 매우 일반적인 로직이다.

### Bean Validation 이란?

먼저 Bean Validation은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380)이라는 기술 표준이다.
쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다.
마치 JPA가 표준 기술이고 그 구현체로 하이버네이트가 있는 것과 같다.

Bean Validation을 구현한 기술중에 일반적으로 사용하는 구현체는 하이버네이트 Validator이다.
이름이 하이버네이트가 붙어서 그렇지 ORM과는 관련이 없다.

### 하이버네이트 Validator 관련 링크

* [공식 사이트](http://hibernate.org/validator/)
* [공식 메뉴얼](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/)
* [검증 애노테이션 모음](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-defineconstraints-spec)

## 시작

### 의존관계 추가

`build.gradle`

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### Item Class

```java
@Getter
@Setter
@ToString
public class Item {
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 100000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

### 검증 애노테이션

* `@NotBlank` : 빈값 + 공백만 있는 경우를 허용하지 않는다.
* `@NotNull` : null 을 허용하지 않는다.
* `@Range(min = 1000, max = 1000000)` : 범위 안의 값이어야 한다.
* `@Max(9999)` : 최대 9999까지만 허용한다.

> **참고**<br>
> * `javax.validation.constraints.NotNull`
> * `org.hibernate.validator.constraints.Range`
>
> `javax.validation`으로 시작하면 특정 구현에 관계없이 제공되는 표준 인터페이스이고,
> `org.hibernate.validator`로 시작하면 하이버네이트 `validator` 구현체를 사용할 때만 제공되는 검증 기능이다.
> 실무에서 대부분 하이버네이트 `validator`를 사용하므로 자유롭게 사용해도 된다.

### BeanValidationText

```java
public class BeanValidationTest {
    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(100000);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }
    }
}
```

### 결과

```
violation = ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.springcoremvc22.domain.validation.Item, messageTemplate='{jakarta.validation.constraints.NotBlank.message}'}
violation.getMessage() = 공백일 수 없습니다
violation = ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.springcoremvc22.domain.validation.Item, messageTemplate='{jakarta.validation.constraints.Max.message}'}
violation.getMessage() = 9999 이하여야 합니다
violation = ConstraintViolationImpl{interpolatedMessage='1000에서 100000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.springcoremvc22.domain.validation.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
violation.getMessage() = 1000에서 100000 사이여야 합니다
```

## 프로젝트 준비 V3

## 스프링 적용

### 스프링 MVC는 어떻게 Bean Validator를 사용할까?

스프링 부트가 `spring-boot-starter-validation` 라이브러리를 넣으면
자동으로 Bean Validator를 인지하고 스프링에 통합한다.

### 스프링 부트는 자동으로 글로벌 Validator를 등록한다.

`LocalValidatorFactoryBean`을 글로벌 Validator로 등록한다.
이 Validator는 `@NotNull` 같은 애노테이션을 보고 검증을 수행한다.
이렇게 글로벌 Validator가 적용되어 있기 때문에, `@Valid`, `@Validated`만 적용하면 된다.
검증 오류가 발생하면, `FieldError`, `ObjectError`를 생성해서 `BindingResult`에 담아준다.

### 검증 순서

1. `@ModelAttribute` 각각의 플드에 타입 변환 시도
    1. 성공하면 다음으로
    2. 실패하면 `typeMismatch`로 `FieldError` 추가
2. Validator 적용

### 바인딩에 성공한 필드만 Bean Validator 적용

BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.
생각해보면 타입 변환에 성공해서 바인딩에 성공한 필드여야 BeanValidation 적용이 의미 있다.
(일단 모델 객체에 바인딩 받는 값이 정상으로 들어와야 검증도 의미가 있다.)

## 에러 코드

### NotBlank

* NotBlank.item.itemName
* NotBlank.itemName
* NotBlank.java.lang.String
* NotBlank

### Range

* Range.item.price
* Range.price
* Range.java.lang.Integer
* Range

### errors.properties

```properties
# == Bean Validation ==
NotNull                        = {0}, 값을 입력해야 합니다.
NotBlank                       = {0} 공백 X
Range                          = {0}, {2} ~ {1} 허용
Max                            = {0}, 최대 
```

### Bean Validation 메시지 찾는 순서

1. 생성된 메시지 코드 순서대로 messageSource에서 메시지 찾기
2. 애노테이션의 message 속성 사용
3. 라이브러리가 제공하는 기본 값 사용

## 오브젝트 오류

### ScriptAssert

```java
@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
public class Item {
}
```

#### 메시지 코드

* ScriptAssert.item
* ScriptAssert

> 주의! <br>
> 위 코드는 JDK 11 버전에서만 가능하다.
> 즉, 17 버전을 사용하는 Spring boot 3.0 이상은 사용할 수 없다.

### ValidationItemController V3

```java
@PostMapping("/add")
public String addItem(
        @Validated @ModelAttribute Item item,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
) {
    // 특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if (bindingResult.hasErrors()) {
        return "validation/v3/addForm";
    }

    // 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v3/items/{itemId}";
}
```

ScriptAssert는 제약이 많고 복잡하여, 오브젝트 오류의 경우 저번 시간에 했던 방식으로 직접 작성하는 것을 권장한다.

## 수정에 적용

### ValidationItemController V3

```java
@PostMapping("/{itemId}/edit")
public String edit(
        @PathVariable long itemId,
        @Validated @ModelAttribute Item item,
        BindingResult bindingResult
) {
    // 특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if (bindingResult.hasErrors()) {
        return "validation/v3/editForm";
    }

    // 성공 로직
    itemRepository.update(itemId, item);
    return "redirect:/validation/v3/items/{itemId}";
}
```

### editForm.html

```html
<form action="item.html" method="post" th:action th:object="${item}">
    <div th:if="${#fields.hasGlobalErrors()}">
        <p class="field-error" th:each="err : ${#fields.globalErrors()}" th:text="${err}"></p>
    </div>

    <div>
        <label for="id">[[#{label.item.id}]]</label>
        <input class="form-control" readonly th:field="*{id}" type="text">
    </div>
    <div>
        <label for="itemName">[[#{label.item.itemName}]]</label>
        <input class="form-control"
               th:errorclass="field-error"
               th:field="*{itemName}"
               type="text">
        <div class="field-error" th:errors="*{itemName}"></div>
    </div>
    <div>
        <label for="price">[[#{label.item.price}]]</label>
        <input class="form-control"
               th:errorclass="field-error"
               th:field="*{price}"
               type="text">
        <div class="field-error" th:errors="*{price}"></div>
    </div>
    <div>
        <label for="quantity">[[#{label.item.quantity}]]</label>
        <input class="form-control"
               th:errorclass="field-error"
               th:field="*{quantity}"
               type="text">
        <div class="field-error" th:errors="*{quantity}"></div>
    </div>
</form>
```

## 한계

### 수정시 요구사항

* 등록 시에는 수량을 최대 9999까지 등록할 수 있지만, 수정 시에는 수량을 무제한으로 변경할 수 있다.
* 등록 시에는 id에 값이 없어도 되지만, 수정 시에는 id 값이 필수이다.

### 적용 - Item

```java
@Getter
@Setter
@ToString
public class Item {
    @NotNull // 수정 요구사항 추가
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 100000)
    private Integer price;

    @NotNull
//    @Max(9999) // 수정 요구사항 추가
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

### 문제

수정은 됐다.
그런데 이러면 등록도 같이 적용이 되어 문제가 생긴다.

## groups

### 위의 문제를 해결하는 방법은 2가지가 있다.

1. BeanValidation의 groups 기능을 사용한다.
2. Item을 직접 사용하지 않고, ItemSaveForm, ItemUpdateForm 같은 폼 전송을 위한 별도의 모델 객체를 만들어서 사용한다. (DTO)

### groups 기능 사용

#### SaveCheck 인터페이스

```java
public interface SaveCheck {
}
```

#### UpdateCheck 인터페이스

```java
public interface UpdateCheck {
}
```

### Item - groups 적용

```java
@Getter
@Setter
@ToString
public class Item {
    @NotNull(groups = UpdateCheck.class)
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 100000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = SaveCheck.class)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

### ValidationItemController V3

```java
@PostMapping("/add")
public String addItem(
        @Validated(SaveCheck.class) @ModelAttribute Item item,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
) { ... }

@PostMapping("/{itemId}/edit")
public String edit(
        @PathVariable long itemId,
        @Validated(UpdateCheck.class) @ModelAttribute Item item,
        BindingResult bindingResult
) { ... }
```

### 정리

groups 기능을 사용해서 등록과 수정 시에 각각 다르게 검증할 수 있게되었다.
그런데, 코드를 보면 알겠지만, 작성해야되는 코드가 너무 많고 중복이 너무 많아졌다.
사실 groups 기능은 잘 사용되지 않는데, 그 이유는 위의 방법보다 그냥 등록용 객체와 수정용 객체를 따로 만들어서 관리하기 때문이다.

## Form 전송 객체 분리 - 프로젝트 준비 V4

## Form 전송 객체 분리 - 소개

실무에서는 `groups`를 잘 사용하지 않는데, 그 이유가 다른 곳에 있다.
바로 등록시 폼에서 전달되는 데이터가 `Item` 도메인 객체와 딱 맞지 않기 때문이다.

현재 작성하는 코드처럼 간단한 예제같은 경우는 폼에서 전달하는 데이터와 `Item` 도메인 객체가 딱 맞는다.
하지만, 실무에서는 회원 등록시 회원과 관련된 데이터만 받는게 아니라, 약관 정보도 추가로 받는 등 `Item`과 관계없는 수많은 부가 데이터가 넘어온다.

그래서 보통 `Item`을 직접 전달받는 것이 아니라, 복잡한 폼의 데이터를 컨트롤러까지 전달할 별도의 객체를 만들어서 전달한다.
예를 들면, `ItemSaveForm` 이라는 폼을 전달 받는 전용 객체를 만들어서 `@ModelAttribute`로 사용한다.
이것을 통해 컨트롤러에서 폼 데이터를 전달받고, 이후 컨트롤러에서 필요한 데이터를 사용해서 `Item`을 생성한다.

### 폼 데이터 전달에 Item 도메인 객체 사용

```
HTML Form -> Item -> Controller -> Item -> Repository
```

* 장점: `Item` 도메인 객체를 컨트롤러, 리포지토리까지 직접 전달해서 중간에 `Item`을 만드는 과정이 없어서 간단하다.
* 단점: 간단한 경우에만 적용할 수 있다. 수정시 검증이 중복될 수 있고, `groups`를 사용해야 한다.

### 폼 데이터 전달을 위한 별도의 객체 사용

```
HTML Form -> ItemSaveForm -> Controller -> Item -> Repository
```

* 장점: 전송하는 폼 데이터가 복잡해도 거기에 맞춘 별도의 폼 객체를 사용해서 데이터를 전달 받을 수 있다.
  보통 등록과, 수정용으로 별도의 폼 객체를 만들기 때문에 검증이 중복되지 않는다.
* 단점: 폼 데이터를 기반으로 컨트롤러에서 `Item` 객체를 생성하는 변환 과정이 추가된다.

### 정리

수정의 경우 등록과 수정은 완전히 다른 데이터가 넘어온다.
생각해보면 회원 가입시 다루는 데이터와 수정시 다루는 데이터는 범위에 차이가 있다.
예를 들면 등록시에는 로그인 id, 주민번호 등등을 받을 수 있지만, 수정시에는 이런 부분이 빠진다.
그리고 검증 로직도 많이 달라진다.
그래서 `ItemUpdateForm` 이라는 별도의 객체로 데이터를 전달받는 것이 좋다.

Item 도메인 객체를 폼 전달 데이터로 사용하고, 그대로 쭉 넘기면 편리하겠지만,
앞에서 설명한 것과 같이 실무에서는 `Item` 의 데이터만 넘어오는 것이 아니라 무수한 추가 데이터가 넘어온다.
그리고 더 나아가서 `Item` 을 생성하는데 필요한 추가 데이터를 데이터베이스나 다른 곳에서 찾아와야 할 수도 있다.

따라서 이렇게 폼 데이터 전달을 위한 별도의 객체를 사용하고,
등록, 수정용 폼 객체를 나누면 등록, 수정이 완전히 분리되기 때문에 `groups` 를 적용할 일은 드물다.

## Form 전송 객체 분리 - 개발

### Item 원복

```java
@Getter @Setter
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

### ItemSaveDto

```java
@Setter @Getter
public class ItemSaveDto {
    @NotNull
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
```

### ItemUpdateDto

```java
@Getter @Setter
public class ItemUpdateDto {
    @NotNull
    private Long id;

    @NotNull
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    private Integer quantity;
}
```

### ValidationItemController V4

```java
@PostMapping("/add")
public String addItem(
        @Validated @ModelAttribute("item") ItemSaveDto item,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
) {
    // 특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if (bindingResult.hasErrors()) {
        return "validation/v4/addForm";
    }

    // 성공 로직
    Item savedItem = itemRepository.save(new Item(
            item.getItemName(), item.getPrice(), item.getQuantity()
    ));
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v4/items/{itemId}";
}

@PostMapping("/{itemId}/edit")
public String edit(
        @Validated @ModelAttribute("item") ItemUpdateDto item,
        BindingResult bindingResult
) {
    // 특정 필드가 아닌 복합 룰 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }

    // 검증에 실패하면 다시 입력 폼으로
    if (bindingResult.hasErrors()) {
        return "validation/v4/editForm";
    }

    // 성공 로직
    itemRepository.update(item.getId(), new Item(
            item.getItemName(), item.getPrice(), item.getQuantity()
    ));
    return "redirect:/validation/v4/items/{itemId}";
}
```

> **주의!** <br>
> `@ModelAttribute("item")`에 item 이름을 넣어준 부분을 주의하자.
> 이것을 넣지않으면, `ItemSaveForm`의 경우 규칙에 의해 `itemSaveForm`이라는 이름으로 MVC Model에 담기게 된다.
> 이렇게 되면, 뷰 템플릿에서 접근하는 `th:object`이름도 함께 변경해주어야 한다.

## HTTP 메시지 컨버터
