package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.request.ReviewRequest;
import Bus_Ticket_booking.ijse.DTO.response.ReviewResponse;
import Bus_Ticket_booking.ijse.entity.Review;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, String username);
    List<ReviewResponse> getReviewsByRoute(Long routeId);
    List<ReviewResponse> getMyReviews(String username);
    void deleteReview(Long id);
    ReviewResponse toResponse(Review review);
}
