package hello.springcoremvc22.domain.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * FAST: 빠른 배송
 * NORMAL: 일반 배송
 * SLOW: 느린 배송
 */
@Getter @Setter
@AllArgsConstructor
public class DeliveryCode {
    private String code;
    private String displayName;
}
