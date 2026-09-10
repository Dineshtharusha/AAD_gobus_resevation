package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.RouteRequest;
import Bus_Ticket_booking.ijse.DTO.response.RouteResponse;
import Bus_Ticket_booking.ijse.entity.Route;

import java.util.List;

public interface RouteService {
    List<RouteResponse> getAllActiveRoutes();
    List<RouteResponse> getAllRoutes();
    RouteResponse getRouteById(Long id);
    List<RouteResponse> searchRoutes(String source, String destination);
    RouteResponse createRoute(RouteRequest request);
    RouteResponse updateRoute(Long id, RouteRequest request);
    void deleteRoute(Long id);
    RouteResponse toResponse(Route route);
}
