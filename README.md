Project report === https://docs.google.com/document/d/1DLhIkr2id9BBOF0ISrB2uLkZZ8HRGWE9/edit

Frontend Structure
Located in src/main/resources/static/:

index.html: Landing page and route search.

login.html / register.html: User authentication interfaces.

user-dashboard.html: Passenger view for managing upcoming and past bookings.

admin-dashboard.html: Administrative interface for managing the fleet, routes, and overall system.

booking-confirmation.html: Checkout and payment summary page.

/css & /js: Modularized styling and scripts for the respective views.

⚙️ Key Features
Role-Based Access Control: Distinct views and permissions for Users and Admins.

Dynamic Seat Booking: Real-time seat availability checking and reservation.

Route & Schedule Management: Complex routing with multiple stops and scheduled departures.

Payment Processing Integration: Dedicated endpoints and entity tracking for transactions.

Cancellations & Refunds: Automated workflows for ticket cancellations.

Reviews & Notifications: Passenger feedback system and automated alerts.

🚦 Getting Started
Prerequisites
Java Development Kit (JDK) 17 or higher

Maven installed (or use the included mvnw wrapper)

A configured relational database (e.g., MySQL, PostgreSQL) matching the application.properties configuration.