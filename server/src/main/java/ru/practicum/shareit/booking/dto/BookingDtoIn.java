package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingDtoIn {
    LocalDateTime start;
    @JsonProperty("end")
    LocalDateTime ending;
    Long itemId;
    @Builder.Default
    Status status = Status.WAITING;
}
