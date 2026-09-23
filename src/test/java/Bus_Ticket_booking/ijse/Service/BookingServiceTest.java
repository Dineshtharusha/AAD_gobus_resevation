package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.response.BookingResponse;
import Bus_Ticket_booking.ijse.Service.Serviceimpl.BookingServiceImpl;
import Bus_Ticket_booking.ijse.entity.*;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import Bus_Ticket_booking.ijse.enumaration.RoleName;
import Bus_Ticket_booking.ijse.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingSeatRepository bookingSeatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private BusSeatRepository busSeatRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User ownerUser;
    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        Role ownerRole = Role.builder().id(3L).name(RoleName.ROLE_BUS_OWNER).build();
        ownerUser = User.builder()
                .id(5L)
                .username("busowner")
                .email("owner@gobus.lk")
                .roles(Set.of(ownerRole))
                .build();

        User passengerUser = User.builder()
                .id(10L)
                .username("testuser")
                .email("passenger@example.com")
                .build();

        Bus bus = Bus.builder()
                .id(1L)
                .busName("GoBus Express")
                .busNumber("NB-1234")
                .owner(ownerUser)
                .build();

        Route route = Route.builder()
                .id(1L)
                .source("Colombo")
                .destination("Kandy")
                .build();

        Schedule schedule = Schedule.builder()
                .id(1L)
                .bus(bus)
                .route(route)
                .departureTime(LocalDateTime.now().plusHours(2))
                .arrivalTime(LocalDateTime.now().plusHours(6))
                .fare(new BigDecimal("1500.00"))
                .build();

        Passenger passenger = Passenger.builder()
                .id(1L)
                .firstName("Kasun")
                .lastName("Silva")
                .nic("199512345678")
                .phone("+94771234567")
                .build();

        sampleBooking = Booking.builder()
                .id(100L)
                .user(passengerUser)
                .schedule(schedule)
                .passenger(passenger)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .totalAmount(new BigDecimal("3000.00"))
                .build();
    }

    @Test
    void testGetBookingsForOwner_returnsPassengerContacts() {
        when(userRepository.findByUsername("busowner")).thenReturn(Optional.of(ownerUser));
        when(bookingRepository.findByScheduleBusOwnerUsername("busowner")).thenReturn(List.of(sampleBooking));
        when(bookingSeatRepository.findByBookingId(anyLong())).thenReturn(new ArrayList<>());

        List<BookingResponse> responses = bookingService.getBookingsForOwner("busowner", null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        BookingResponse r = responses.get(0);
        assertEquals("Kasun Silva", r.getPassengerName());
        assertEquals("+94771234567", r.getPassengerPhone());
        assertEquals("passenger@example.com", r.getPassengerEmail());
        assertEquals("NB-1234", r.getBusNumber());
        assertEquals("Colombo", r.getSource());
        assertEquals("Kandy", r.getDestination());
    }
}
