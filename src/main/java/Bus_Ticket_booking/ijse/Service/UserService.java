package Bus_Ticket_booking.ijse.Service;

import Bus_Ticket_booking.ijse.DTO.response.UserResponse;
import Bus_Ticket_booking.ijse.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void toggleUserStatus(Long id);
    void deleteUser(Long id);
    UserResponse toResponse(User user);
}
