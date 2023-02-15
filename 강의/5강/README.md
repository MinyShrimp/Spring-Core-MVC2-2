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

## 한계

## groups

## Form 전송 객체 분리 - 프로젝트 준비 V4

## Form 전송 객체 분리 - 소개

## Form 전송 객체 분리 - 개발

## HTTP 메시지 컨버터
