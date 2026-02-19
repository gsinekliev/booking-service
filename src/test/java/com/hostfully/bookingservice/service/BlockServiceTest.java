package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.repository.BlockRepository;
import com.hostfully.bookingservice.repository.BookingRepository;
import com.hostfully.bookingservice.util.DateChecker;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BlockServiceTest {
    @Mock
    private BlockRepository blockRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private DateChecker dateChecker;

    private BlockService blockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        blockService = new BlockService(bookingRepository, blockRepository, dateChecker);
    }
}