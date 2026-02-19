package com.hostfully.bookingservice.repository;

import com.hostfully.bookingservice.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findAllByPropertyId(Long propertyId);
}
