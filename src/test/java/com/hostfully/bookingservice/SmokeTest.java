package com.hostfully.bookingservice;

import com.hostfully.bookingservice.dto.BlockRequest;
import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.model.Block;
import com.hostfully.bookingservice.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void createGetDeleteBooking() {
        BookingRequest request = new BookingRequest();
        request.setGuestName("John Doe");
        request.setGuestEmail("john@example.com");
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(5));

        // Create — expect 201
        Long id = webTestClient.post()
                .uri("/api/v1/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Booking.class)
                .returnResult()
                .getResponseBody()
                .getId();

        // Get — expect 200
        webTestClient.get()
                .uri("/api/v1/bookings/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id);

        // Delete — expect 204
        webTestClient.delete()
                .uri("/api/v1/bookings/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void createDeleteBlock() {
        BlockRequest request = new BlockRequest();
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(5));
        request.setReason("Maintenance");

        // Create — expect 201
        Long id = webTestClient.post()
                .uri("/api/v1/blocks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Block.class)
                .returnResult()
                .getResponseBody()
                .getId();

        // Delete — expect 204
        webTestClient.delete()
                .uri("/api/v1/blocks/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
