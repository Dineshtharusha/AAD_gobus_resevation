package Bus_Ticket_booking.ijse.Service.Serviceimpl;

import Bus_Ticket_booking.ijse.DTO.request.ReviewRequest;
import Bus_Ticket_booking.ijse.DTO.response.ReviewResponse;
import Bus_Ticket_booking.ijse.Service.ReviewService;
import Bus_Ticket_booking.ijse.entity.Review;
import Bus_Ticket_booking.ijse.entity.Route;
import Bus_Ticket_booking.ijse.entity.User;
import Bus_Ticket_booking.ijse.enumaration.RoleName;
import Bus_Ticket_booking.ijse.exception.BadRequestException;
import Bus_Ticket_booking.ijse.exception.ResourceNotFoundException;
import Bus_Ticket_booking.ijse.exception.UnauthorizedException;
import Bus_Ticket_booking.ijse.repository.ReviewRepository;
import Bus_Ticket_booking.ijse.repository.RouteRepository;
import Bus_Ticket_booking.ijse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        Review review = reviewRepository.findByUserIdAndRouteId(user.getId(), route.getId())
                .orElse(null);

        if (review != null) {
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setCreatedAt(LocalDateTime.now());
        } else {
            review = Review.builder()
                    .user(user)
                    .route(route)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }

        Review saved = reviewRepository.save(review);
        log.info("Review {} saved by {} for route {}", saved.getId(), username, route.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByRoute(Long routeId) {
        return reviewRepository.findByRouteId(routeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return reviewRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", "id", id);
        }
        reviewRepository.deleteById(id);
        log.info("Review {} deleted", id);
    }

    @Override
    @Transactional
    public void deleteReview(Long id, String username) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);

        if (!isAdmin && !review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
        log.info("Review {} deleted by {}", id, username);
    }

    @Override
    public ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .routeId(review.getRoute().getId())
                .routeSummary(review.getRoute().getSource() + " → " + review.getRoute().getDestination())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
