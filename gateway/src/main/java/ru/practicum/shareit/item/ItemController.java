package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.error.validation.Update;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@Validated({Create.class}) @RequestBody ItemDtoIn itemDtoIn,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта POST /items(X-Sharer-User-Id=" + userId + ").");
        return itemClient.addItem(itemDtoIn, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId,
                                                 @Validated({Update.class}) @RequestBody ItemDtoIn itemDtoIn,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта PATCH /items/" + itemId + "(X-Sharer-User-Id=" + userId + ").");
        return itemClient.updateItem(itemDtoIn, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта GET /items/(X-Sharer-User-Id=" + userId + ")" + itemId + ".");
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", required = false,
                                                             defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", required = false,
                                                             defaultValue = "10") Integer size) {
        log.info("Обработка эндпойнта GET /items(X-Sharer-User-Id=" + userId + ").");
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String text,
                                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", required = false,
                                                                defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", required = false,
                                                                defaultValue = "10") Integer size) {
        log.info("Обработка эндпойнта GET /items/search?text=" + text + "(X-Sharer-User-Id=" + userId + ").");
        return itemClient.searchAvailableItemsByPartOfNameOrDescription(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Обработка эндпойнта POST /items/{itemId=" + itemId + "}/comment(X-Sharer-User-Id=" + userId + ").");
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
