package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.error.validation.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@Validated({Create.class}) @RequestBody BookingDtoIn bookingDtoIn,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта POST /bookings(X-Sharer-User-Id=" + userId + ").");
        return bookingClient.addBooking(bookingDtoIn, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @PathVariable long bookingId,
                                                             @RequestParam(name = "approved") String approved) {
        log.info("Обработка эндпойнта PATCH /bookings/{bookingId=" + bookingId + "}?approved=" + approved
                + "(X-Sharer-User-Id=" + userId + ").");
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long bookingId) {
        log.info("Обработка эндпойнта GET /bookings/{bookingId=" + bookingId + "(X-Sharer-User-Id=" + userId + ").");
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingOfUser(@RequestParam(name = "state", required = false,
            defaultValue = "ALL") String state,
                                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                                   @PositiveOrZero @RequestParam(name = "from", required = false,
                                                                           defaultValue = "0") Integer from,
                                                                   @Positive @RequestParam(name = "size", required = false,
                                                                           defaultValue = "10") Integer size) {

        log.info("Обработка эндпойнта GET /bookings?state=" + state + "(X-Sharer-User-Id=" + userId + ").");
        return bookingClient.getBookingsByUserId(State.stringToEnum(state.toUpperCase()), userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingOfOwner(@RequestParam(name = "state", required = false,
            defaultValue = "ALL") String state,
                                                                    @RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                    @PositiveOrZero @RequestParam(name = "from", required = false,
                                                                            defaultValue = "0") Integer from,
                                                                    @Positive @RequestParam(name = "size", required = false,
                                                                            defaultValue = "10") Integer size) {
        log.info("Обработка эндпойнта GET /bookings/owner?state=" + state + "(X-Sharer-User-Id=" + ownerId + ").");
        return bookingClient.getBookingsByOwnerId(State.stringToEnum(state.toUpperCase()), ownerId, from, size);
    }
}
