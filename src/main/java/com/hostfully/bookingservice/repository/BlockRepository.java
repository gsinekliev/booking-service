package com.hostfully.bookingservice.repository;

import com.hostfully.bookingservice.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
