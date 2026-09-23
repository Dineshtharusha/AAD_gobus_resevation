package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.BusRequest;
import Bus_Ticket_booking.ijse.DTO.response.BusResponse;
import Bus_Ticket_booking.ijse.Service.BusService;
import Bus_Ticket_booking.ijse.entity.Bus;
import Bus_Ticket_booking.ijse.entity.BusSeat;
import Bus_Ticket_booking.ijse.enumaration.SeatType;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.repository.BusRepository;
import Bus_Ticket_booking.ijse.repository.BusSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusSeatRepository busSeatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getMyBuses(String username) {
        return busRepository.findByOwnerUsernameAndActiveTrue(username).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        return toResponse(bus);
    }

    @Override
    @Transactional
    public BusResponse createBus(BusRequest request) {
        if (busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new BadRequestException("Bus number '" + request.getBusNumber() + "' already exists");
        }

        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .busName(request.getBusName())
                .busType(request.getBusType())
                .totalSeats(request.getTotalSeats())
                .build();

        Bus savedBus = busRepository.save(bus);

        // Auto-generate seat layout
        List<BusSeat> seats = new ArrayList<>();
        for (int i = 1; i <= request.getTotalSeats(); i++) {
            BusSeat seat = BusSeat.builder()
                    .bus(savedBus)
                    .seatNumber(String.valueOf(i))
                    .seatType(i % 3 == 0 ? SeatType.AISLE : SeatType.WINDOW)
                    .build();
            seats.add(seat);
        }
        busSeatRepository.saveAll(seats);
        log.info("Created bus: {} with {} seats", savedBus.getBusNumber(), seats.size());
        return toResponse(savedBus);
    }

    @Override
    @Transactional
    public BusResponse updateBus(Long id, BusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        bus.setBusName(request.getBusName());
        bus.setBusType(request.getBusType());
        log.info("Updated bus id: {}", id);
        return toResponse(busRepository.save(bus));
    }

    @Override
    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", id));
        bus.setActive(false);
        busRepository.save(bus);
        log.info("Soft-deleted bus id: {}", id);
    }

    @Override
    public BusResponse toResponse(Bus bus) {
        return BusResponse.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .busName(bus.getBusName())
                .busType(bus.getBusType())
                .totalSeats(bus.getTotalSeats())
                .active(bus.isActive())
                .build();
    }
}
