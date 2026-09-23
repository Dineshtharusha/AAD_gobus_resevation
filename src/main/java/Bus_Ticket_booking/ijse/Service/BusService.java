package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.BusRequest;
import Bus_Ticket_booking.ijse.DTO.response.BusResponse;
import Bus_Ticket_booking.ijse.entity.Bus;

import java.util.List;

public interface BusService {
    List<BusResponse> getAllBuses();
    List<BusResponse> getMyBuses(String username);
    BusResponse getBusById(Long id);
    BusResponse createBus(BusRequest request);
    BusResponse updateBus(Long id, BusRequest request);
    void deleteBus(Long id);
    BusResponse toResponse(Bus bus);
}
