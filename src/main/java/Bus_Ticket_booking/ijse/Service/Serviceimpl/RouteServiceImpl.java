package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.RouteRequest;
import Bus_Ticket_booking.ijse.DTO.response.RouteResponse;
import Bus_Ticket_booking.ijse.Service.RouteService;
import Bus_Ticket_booking.ijse.entity.Route;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.repository.ReviewRepository;
import Bus_Ticket_booking.ijse.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getAllActiveRoutes() {
        return routeRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        return toResponse(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> searchRoutes(String source, String destination) {
        return routeRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        Route route = Route.builder()
                .source(request.getSource())
                .destination(request.getDestination())
                .distanceKm(request.getDistanceKm())
                .durationMinutes(request.getDurationMinutes())
                .baseFare(request.getBaseFare())
                .build();
        Route saved = routeRepository.save(route);
        log.info("Created route: {} -> {}", saved.getSource(), saved.getDestination());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        route.setSource(request.getSource());
        route.setDestination(request.getDestination());
        route.setDistanceKm(request.getDistanceKm());
        route.setDurationMinutes(request.getDurationMinutes());
        route.setBaseFare(request.getBaseFare());
        log.info("Updated route id: {}", id);
        return toResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        route.setActive(false);
        routeRepository.save(route);
        log.info("Soft-deleted route id: {}", id);
    }

    @Override
    public RouteResponse toResponse(Route route) {
        Double avgRating = reviewRepository.findAverageRatingByRouteId(route.getId());
        return RouteResponse.builder()
                .id(route.getId())
                .source(route.getSource())
                .destination(route.getDestination())
                .distanceKm(route.getDistanceKm())
                .durationMinutes(route.getDurationMinutes())
                .baseFare(route.getBaseFare())
                .active(route.isActive())
                .averageRating(avgRating)
                .build();
    }
}
