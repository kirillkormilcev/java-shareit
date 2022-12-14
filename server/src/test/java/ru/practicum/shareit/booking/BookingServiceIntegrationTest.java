package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.exception.BookingValidationException;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.ItemValidationException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareitBooking",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.ru")
            .build();

    User user3 = User.builder()
            .id(3L)
            .name("user3")
            .email("user3@email.ru")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .owner(user1)
            .build();

    Booking booking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 10, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 10, 16, 10, 0, 0))
            .item(item1)
            .booker(user2)
            .status(Status.WAITING)
            .build();

    BookingDtoIn bookingDtoIn1 = BookingDtoIn.builder()
            .start(LocalDateTime.of(2022, 10, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 10, 16, 10, 0, 0))
            .itemId(1L)
            .status(Status.WAITING)
            .build();

    @Test
    void testUpdateBookingStatusAndAllExceptions() {

        bookingService.getItemRepository().save(item1);
        bookingService.getUserRepository().save(user2);

        bookingService.addBooking(bookingDtoIn1, user2.getId());
        bookingService.updateBookingStatus(user1.getId(), booking1.getId(), "true");

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);

        Booking booking = query
                .setParameter("id", booking1.getId())
                .getSingleResult();

        assertThat(booking.getStatus(), equalTo(Status.APPROVED));

        IncorrectRequestParamException ex1 = assertThrows(IncorrectRequestParamException.class, () ->
                bookingService.updateBookingStatus(user1.getId(), booking1.getId(), "true"));

        assertEquals(IncorrectRequestParamException.class, ex1.getClass());
        assertEquals("???????????????????????? ?????? ????????????????.", ex1.getMessage());

        bookingService.updateBookingStatus(user1.getId(), booking1.getId(), "false");
        IncorrectRequestParamException ex2 = assertThrows(IncorrectRequestParamException.class, () ->
                bookingService.updateBookingStatus(user1.getId(), booking1.getId(), "false"));

        assertEquals(IncorrectRequestParamException.class, ex2.getClass());
        assertEquals("???????????????????????? ?????? ??????????????????.", ex2.getMessage());

        String wrongParameter = "wrong";
        BookingValidationException ex3 = assertThrows(BookingValidationException.class, () ->
                bookingService.updateBookingStatus(user1.getId(), booking1.getId(), wrongParameter));

        assertEquals(BookingValidationException.class, ex3.getClass());
        assertEquals("?????????????? ???? ???????????? ???????????????? approved = " + wrongParameter + ".\n" +
                "???????????? ???????? true ?????? false.", ex3.getMessage());

        NotFoundException ex4 = assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(bookingDtoIn1, user1.getId()));

        assertEquals(NotFoundException.class, ex4.getClass());
        assertEquals("???????????? ???????????????????? ?? ???????????? ????????.", ex4.getMessage());

        NotFoundException ex5 = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsByOwnerId(State.ALL, user2.getId(), 0, 10));

        assertEquals(NotFoundException.class, ex5.getClass());
        assertEquals("???? ???? ???????????????? ???? ?????????? ??????????.", ex5.getMessage());

        NotFoundException ex7 = assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(user2.getId(), 100L, "true"));

        assertEquals(NotFoundException.class, ex7.getClass());
        assertEquals("???????????? ?? ???????????????? 100 ???? ?????????????? ?? ????????.", ex7.getMessage());

        bookingService.getUserRepository().save(user3);
        NotFoundException ex8 = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(user3.getId(), booking1.getId()));

        assertEquals(NotFoundException.class, ex8.getClass());
        assertEquals("???? ???? ?????????????????? ???????????? ?? ????????????????: " + booking1.getId() + ". \n" +
                "?????? ???? ???? ???????????? ???????? ?? ????????????????: " + item1.getId() + ".", ex8.getMessage());

        NotFoundException ex9 = assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(user3.getId(), booking1.getId(), "false"));

        assertEquals(NotFoundException.class, ex9.getClass());
        assertEquals("???? ???? ???????????? ???????? ?? ????????????????: " + booking.getItem().getId() + ".", ex9.getMessage());

        item1.setAvailable(false);
        bookingService.getItemRepository().save(item1);
        ItemValidationException ex10 = assertThrows(ItemValidationException.class, () ->
                bookingService.addBooking(bookingDtoIn1, user2.getId()));

        assertEquals(ItemValidationException.class, ex10.getClass());
        assertEquals("???????? ?? ???????????????? " + bookingDtoIn1.getItemId() + " ?? ?????????????????? ???????????? ???? ????????????????.", ex10.getMessage());

        bookingService.getItemRepository().delete(item1);
        NotFoundException ex6 = assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(user2.getId(), booking1.getId(), "true"));

        assertEquals(NotFoundException.class, ex6.getClass());
        assertEquals("???????? ???? ?????????????? ?? ????????.", ex6.getMessage());

        NotFoundException ex11 = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsByOwnerId(State.ALL, 100L, 0, 10));

        assertEquals(NotFoundException.class, ex11.getClass());
        assertEquals("???????????????????????? ?? ???????????????? 100 ???? ???????????? ?? ????????.", ex11.getMessage());
    }
}