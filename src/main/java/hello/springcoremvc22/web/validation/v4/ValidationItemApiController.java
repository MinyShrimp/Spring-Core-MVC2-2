package hello.springcoremvc22.web.validation.v4;

import hello.springcoremvc22.dto.item.ItemSaveDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {
    @PostMapping("/add")
    public Object addItem(
            @Validated @RequestBody ItemSaveDto item,
            BindingResult bindingResult
    ) {
        log.info("POST /validation/api/items/add 호출");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생: errors = {}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("POST /validation/api/items/add 성공");
        return item;
    }
}
