package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.error.validation.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Обработка эндпойнта POST /users.");
        return userClient.addUserToStorage(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Обработка эндпойнта GET /users/" + userId + ".");
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Обработка эндпойнта GET /users.");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                              @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Обработка эндпойнта PATCH /users/" + userId + ".");
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Обработка эндпойнта DELETE /users/" + userId + ".");
        return userClient.deleteUserById(userId);
    }
}
