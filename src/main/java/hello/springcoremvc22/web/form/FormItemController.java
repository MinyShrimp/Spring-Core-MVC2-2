package hello.springcoremvc22.web.form;

import hello.springcoremvc22.domain.item.DeliveryCode;
import hello.springcoremvc22.domain.item.Item;
import hello.springcoremvc22.domain.item.ItemRepository;
import hello.springcoremvc22.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {
    private final ItemRepository itemRepository;
    private final MessageSource ms;

    @ModelAttribute("regions")
    public Map<String, String> regions(
            Locale locale
    ) {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", ms.getMessage("option.regions.seoul", null, locale));
        regions.put("BUSAN", ms.getMessage("option.regions.busan", null, locale));
        regions.put("JEJU", ms.getMessage("option.regions.jeju", null, locale));
        return regions;
    }

    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        return ItemType.values();
    }

    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes(
            Locale locale
    ) {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", ms.getMessage("option.deliveryCode.fast", null, locale)));
        deliveryCodes.add(new DeliveryCode("NORMAL", ms.getMessage("option.deliveryCode.normal", null, locale)));
        deliveryCodes.add(new DeliveryCode("SLOW", ms.getMessage("option.deliveryCode.slow", null, locale)));
        return deliveryCodes;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    @GetMapping("/{itemId}")
    public String item(
            @PathVariable long itemId,
            Model model
    ) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "form/addForm";
    }

    @PostMapping("/add")
    public String addItem(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes
    ) {
        log.info("item = {}", item.toString());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/form/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(
            @PathVariable long itemId,
            Model model
    ) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(
            @PathVariable long itemId,
            @ModelAttribute Item item
    ) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }
}
