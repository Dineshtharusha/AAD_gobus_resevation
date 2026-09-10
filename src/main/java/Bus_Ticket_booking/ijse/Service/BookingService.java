package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.BookingRequest;
import Bus_Ticket_booking.ijse.DTO.response.BookingResponse;
import Bus_Ticket_booking.ijse.entity.Booking;
import Bus_Ticket_booking.ijse.entity.BookingSeat;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String username);
    List<BookingResponse> getBookingsByUser(String username);
    BookingResponse getBookingById(Long id, String username);
    List<BookingResponse> getAllBookings();
    BookingResponse toResponse(Booking booking, List<BookingSeat> bookingSeats);
}
