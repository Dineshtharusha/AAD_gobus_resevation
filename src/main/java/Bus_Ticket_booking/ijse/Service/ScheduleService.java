package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.ScheduleRequest;
import Bus_Ticket_booking.ijse.DTO.response.ScheduleResponse;
import Bus_Ticket_booking.ijse.DTO.response.SeatAvailabilityResponse;
import Bus_Ticket_booking.ijse.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    List<ScheduleResponse> getAllSchedules();
    ScheduleResponse getScheduleById(Long id);
    List<ScheduleResponse> getSchedulesByRoute(Long routeId, LocalDateTime from, LocalDateTime to);
    List<SeatAvailabilityResponse> getScheduleSeats(Long scheduleId);
    ScheduleResponse createSchedule(ScheduleRequest request);
    ScheduleResponse updateSchedule(Long id, ScheduleRequest request);
    void deleteSchedule(Long id);
    List<ScheduleResponse> getSchedulesForOwner(String username);
    ScheduleResponse toResponse(Schedule s);
}
