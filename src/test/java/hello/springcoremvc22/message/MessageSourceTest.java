package hello.springcoremvc22.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
