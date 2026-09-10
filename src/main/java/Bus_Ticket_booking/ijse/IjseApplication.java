package Bus_Ticket_booking.ijse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IjseApplication {

    public static void main(String[] args) {
        SpringApplication.run(IjseApplication.class, args);
    }
}
