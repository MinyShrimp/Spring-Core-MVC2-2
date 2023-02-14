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

## 에러 코드

## 오브젝트 오류

## 수정에 적용

## 한계

## groups

## Form 전송 객체 분리 - 프로젝트 준비 V4

## Form 전송 객체 분리 - 소개

## Form 전송 객체 분리 - 개발

## HTTP 메시지 컨버터
