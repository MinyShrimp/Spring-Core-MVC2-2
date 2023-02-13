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
스프링은 기본적인 메시지 관리 기능을 제공한다.

메시지 관리 기능을 사용하려면 스프링이 제공하는 `MessageSource`를 스프링 빈으로 등록하면 되는데, `MessageSource`는 인터페이스이다.
따라서 구현체인 `ResourceBundleMessageSource`를 스프링 빈으로 등록하면 된다.

### 직접 등록
```java
@SpringBootApplication
public class SpringCoreMvc22Application {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "errors");
        messageSource.setDefaultEncoding("utf-8");
        return messageSource;
    }
}
```
* `setBasenames`: 설정 파일의 이름을 지정한다.
  * `messages`로 지정하면 `messages.properties` 파일을 읽어서 사용한다.
  * 추가로 국제화 기능을 적용하려면, 별도의 설정없이 파일을 `messages_ko`, `messages_en`과 같이 파일명 마지막에 언어 정보를 주면 된다.
    * 만약, 국제화 파일이 없으면 `messages`를 기본으로 사용한다.
  * 파일의 위치는 `/resources/messages.properties`에 두면 된다.
  * 여러 파일을 한 번에 지정할 수 있다. 여기서는 `messages`, `errors` 둘을 지정했다.
* `setDefaultEncoding`: 인코딩 정보를 지정한다. `utf-8`를 사용하면 된다.

### 스프링 부트
스프링 부트를 사용하면 스프링 부트가 MessageSource를 자동으로 스프링 빈으로 등록한다.

### application.properties
```properties
# /resources/messages 와 /resources/config/i18n/messages 로 설정한다는 의미이다.
spring.messages.basename=messages,config.i18n.messages
```
* 기본값: `spring.messages.basename=messages`

### 메시지 파일 만들기
* messages.properties: 기본 값으로 사용 (한글)
* messages_en.properties: 영어 국제화 사용

```properties
# messages.properties
hello=안녕
hello.name=안녕 {0}
```

```properties
# messages_en.properties
hello=hello
hello.name=hello {0}
```

## 스프링 메시지 소스 사용
### MessageSource 인터페이스
```java
public interface MessageSource {
    @Nullable
    String getMessage(
          String code,
          @Nullable Object[] args,
          @Nullable String defaultMessage,
          Locale locale
    );

    String getMessage(
          String code,
          @Nullable Object[] args,
          Locale locale
    ) throws NoSuchMessageException;

    String getMessage(
          MessageSourceResolvable resolvable,
          Locale locale
    ) throws NoSuchMessageException;
}
```

### 테스트 코드로 확인해보자
```java
@SpringBootTest
public class MessageSourceTest {
    @Autowired
    MessageSource ms;
  
    /**
     * code = hello
     * args = null
     * locale = null
     * => 기본값인 messages.properties
     */
    @Test
    @DisplayName("메시지 가져오기")
    void helloMessage() {
        assertThat(
                ms.getMessage("hello", null, null)
        ).isEqualTo("안녕");
    }
  
    /**
     * "code"가 없는 경우,
     * "NoSuchMessageException" 발생
     */
    @Test
    @DisplayName("메시지가 없는 경우")
    void notFoundMessageCode() {
        assertThatThrownBy(
                () -> ms.getMessage("no_code", null, null)
        ).isInstanceOf(NoSuchMessageException.class);
    }
  
    /**
     * 3번째 인자에 "defaultMessage"를 설정해주면,
     * "code"가 없을 경우 "defaultMessage"를 반환
     */
    @Test
    @DisplayName("Default 메시지를 설정한 경우")
    void notFoundMessageCodeDefaultMessage() {
        assertThat(
                ms.getMessage("no_code", null, "기본 메시지", null)
        ).isEqualTo("기본 메시지");
    }
  
    /**
     * 2번째 인자에 "new Object[]{}"을 이용해 인자를 줄 수 있다.
     *  - hello.name = 안녕 {0}
     *  - => 안녕 Spring
     */
    @Test
    @DisplayName("매개 변수 사용")
    void argumentMessage() {
        assertThat(
                ms.getMessage("hello.name", new Object[]{"Spring"}, null)
        ).isEqualTo("안녕 Spring");
    }
  
    /**
     * "Locale"를 기반으로 국제화 파일을 선택한다.
     * - Locale=en_US => messages_en_US -> messages_en -> messages 순으로 찾는다.
     * 1. "Locale.CHINA"는 없으니 기본값 선택
     * 2. "Locale.ENGLISH"는 있으니 기본값 선택 X
     */
    @Test
    @DisplayName("국제화 파일 선택")
    void langMessage() {
        assertThat(
                ms.getMessage("hello", null, Locale.CHINA)
        ).isEqualTo("안녕");
    
        assertThat(
                ms.getMessage("hello", null, Locale.ENGLISH)
        ).isNotEqualTo("안녕");
    
        assertThat(
                ms.getMessage("hello", null, Locale.ENGLISH)
        ).isEqualTo("hello");
    }
}
```

## 웹 애플리케이션에 메시지 적용하기

## 웹 애플리케이션에 국제화 적용하기
