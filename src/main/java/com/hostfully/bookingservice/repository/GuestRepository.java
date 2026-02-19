package com.hostfully.bookingservice.repository;

import com.hostfully.bookingservice.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
