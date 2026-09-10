package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.ScheduleRequest;
import Bus_Ticket_booking.ijse.DTO.response.ScheduleResponse;
import Bus_Ticket_booking.ijse.DTO.response.SeatAvailabilityResponse;
import Bus_Ticket_booking.ijse.Service.ScheduleService;
import Bus_Ticket_booking.ijse.entity.BookingSeat;
import Bus_Ticket_booking.ijse.entity.Bus;
import Bus_Ticket_booking.ijse.entity.BusSeat;
import Bus_Ticket_booking.ijse.entity.Route;
import Bus_Ticket_booking.ijse.entity.Schedule;
import Bus_Ticket_booking.ijse.enumaration.BookingStatus;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.repository.BookingSeatRepository;
import Bus_Ticket_booking.ijse.repository.BusRepository;
import Bus_Ticket_booking.ijse.repository.BusSeatRepository;
import Bus_Ticket_booking.ijse.repository.RouteRepository;
import Bus_Ticket_booking.ijse.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final BusSeatRepository busSeatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAllActiveWithSeats().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        return toResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByRoute(Long routeId, LocalDateTime from, LocalDateTime to) {
        return scheduleRepository.findAvailableSchedules(routeId, from, to).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatAvailabilityResponse> getScheduleSeats(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        List<BusSeat> busSeats = busSeatRepository.findByBusId(schedule.getBus().getId());
        List<BookingSeat> bookedSeats = bookingSeatRepository.findByBookingScheduleId(scheduleId);
        Set<Long> bookedSeatIds = bookedSeats.stream()
                .filter(bs -> bs.getBooking() != null && 
                        (bs.getBooking().getStatus() == BookingStatus.CONFIRMED || bs.getBooking().getStatus() == BookingStatus.PENDING))
                .map(bs -> bs.getBusSeat().getId())
                .collect(Collectors.toSet());

        return busSeats.stream()
                .sorted(Comparator.comparing(s -> {
                    try {
                        return Integer.parseInt(s.getSeatNumber());
                    } catch (Exception e) {
                        return 0;
                    }
                }))
                .map(seat -> SeatAvailabilityResponse.builder()
                        .id(seat.getId())
                        .seatNumber(seat.getSeatNumber())
                        .seatType(seat.getSeatType())
                        .booked(bookedSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            throw new BadRequestException("Arrival time must be after departure time");
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", request.getBusId()));

        Schedule schedule = Schedule.builder()
                .route(route)
                .bus(bus)
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .fare(request.getFare())
                .availableSeats(request.getAvailableSeats())
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        log.info("Created schedule id: {} for route: {} -> {}", saved.getId(),
                route.getSource(), route.getDestination());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());
        schedule.setFare(request.getFare());
        schedule.setAvailableSeats(request.getAvailableSeats());
        log.info("Updated schedule id: {}", id);
        return toResponse(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        schedule.setActive(false);
        scheduleRepository.save(schedule);
        log.info("Soft-deleted schedule id: {}", id);
    }

    @Override
    public ScheduleResponse toResponse(Schedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .routeId(s.getRoute().getId())
                .source(s.getRoute().getSource())
                .destination(s.getRoute().getDestination())
                .busId(s.getBus().getId())
                .busNumber(s.getBus().getBusNumber())
                .busName(s.getBus().getBusName())
                .departureTime(s.getDepartureTime())
                .arrivalTime(s.getArrivalTime())
                .fare(s.getFare())
                .availableSeats(s.getAvailableSeats())
                .active(s.isActive())
                .build();
    }
}
