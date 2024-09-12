
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AirlineReservationSystem {
    private List<Flight> flights = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private Connection connection;

    public static void main(String[] args) {
        AirlineReservationSystem system = new AirlineReservationSystem();
        system.initializeDatabase();
        system.initializeFlights();
        system.run();
    }

    private void initializeFlights() {
        flights.add(new Flight("F001", "New York", "Los Angeles", "08:00", "11:00", 100));
        flights.add(new Flight("F002", "Los Angeles", "New York", "12:00", "15:00", 100));
        // Add more flights as needed
        saveFlightsToDatabase();
    }

    private void initializeDatabase() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Establish connection to the SQLite database
            connection = DriverManager.getConnection("jdbc:sqlite:reservations.db");
            
            // Create the necessary tables
            createTables();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("SQLite JDBC driver not found.");
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void createTables() throws SQLException {
        String createFlightsTable = "CREATE TABLE IF NOT EXISTS flights (" +
                "flight_number TEXT PRIMARY KEY," +
                "departure TEXT," +
                "destination TEXT," +
                "departure_time TEXT," +
                "arrival_time TEXT," +
                "available_seats INTEGER)";

        String createPassengersTable = "CREATE TABLE IF NOT EXISTS passengers (" +
                "passenger_id TEXT PRIMARY KEY," +
                "name TEXT," +
                "contact_info TEXT)";

        String createReservationsTable = "CREATE TABLE IF NOT EXISTS reservations (" +
                "reservation_id TEXT PRIMARY KEY," +
                "passenger_id TEXT," +
                "flight_number TEXT," +
                "is_cancelled BOOLEAN," +
                "FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id)," +
                "FOREIGN KEY (flight_number) REFERENCES flights(flight_number))";

        try (PreparedStatement pstmt = connection.prepareStatement(createFlightsTable);
             PreparedStatement pstmt2 = connection.prepareStatement(createPassengersTable);
             PreparedStatement pstmt3 = connection.prepareStatement(createReservationsTable)) {
            pstmt.executeUpdate();
            pstmt2.executeUpdate();
            pstmt3.executeUpdate();
        }
    }

    private void saveFlightsToDatabase() {
        String insertFlight = "INSERT OR REPLACE INTO flights (flight_number, departure, destination, departure_time, arrival_time, available_seats) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertFlight)) {
            for (Flight flight : flights) {
                pstmt.setString(1, flight.getFlightNumber());
                pstmt.setString(2, flight.getDeparture());
                pstmt.setString(3, flight.getDestination());
                pstmt.setString(4, flight.getDepartureTime());
                pstmt.setString(5, flight.getArrivalTime());
                pstmt.setInt(6, flight.getAvailableSeats());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void run() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("==========================================================");
            System.out.println("Airline Reservation System");
            System.out.println("1. Book a Flight");
            System.out.println("2. Cancel a Reservation");
            System.out.println("3. View Reservations");
            System.out.println("4. Exit");
            System.out.println("==========================================================");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    bookFlight(scanner);
                    break;
                case 2:
                    cancelReservation(scanner);
                    break;
                case 3:
                    viewReservations();
                    break;
                case 4:
                    System.out.println("Thank you for using the Airline Reservation System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 4);

        scanner.close();
    }

    private void bookFlight(Scanner scanner) {
        System.out.println("Available Flights:");
        for (Flight flight : flights) {
            System.out.println(flight.getFlightNumber() + ": " + flight.getDeparture() + " to " + flight.getDestination() + " (" + flight.getAvailableSeats() + " seats available)");
        }

        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine();
        Flight selectedFlight = null;
        for (Flight flight : flights) {
            if (flight.getFlightNumber().equals(flightNumber)) {
                selectedFlight = flight;
                break;
            }
        }

        if (selectedFlight == null) {
            System.out.println("Flight not found.");
            return;
        }

        if (selectedFlight.getAvailableSeats() <= 0) {
            System.out.println("No available seats on this flight.");
            return;
        }

        System.out.print("Enter passenger name: ");
        String passengerName = scanner.nextLine();
        System.out.print("Enter passenger contact info: ");
        String passengerContactInfo = scanner.nextLine();

        Passenger passenger = new Passenger(generatePassengerId(), passengerName, passengerContactInfo);
        Reservation reservation = new Reservation(generateReservationId(), passenger, selectedFlight);
        reservations.add(reservation);
        selectedFlight.setAvailableSeats(selectedFlight.getAvailableSeats() - 1);

        savePassengerToDatabase(passenger);
        saveReservationToDatabase(reservation);
        updateFlightInDatabase(selectedFlight);


        System.out.println("Reservation successful! Your reservation ID is: " + reservation.getReservationId());
    }

    private void savePassengerToDatabase(Passenger passenger) {
        String insertPassenger = "INSERT INTO passengers (passenger_id, name, contact_info) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertPassenger)) {
            pstmt.setString(1, passenger.getPassengerId());
            pstmt.setString(2, passenger.getName());
            pstmt.setString(3, passenger.getContactInfo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveReservationToDatabase(Reservation reservation) {
        String insertReservation = "INSERT INTO reservations (reservation_id, passenger_id, flight_number, is_cancelled) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertReservation)) {
            pstmt.setString(1, reservation.getReservationId());
            pstmt.setString(2, reservation.getPassenger().getPassengerId());
            pstmt.setString(3, reservation.getFlight().getFlightNumber());
            pstmt.setBoolean(4, reservation.isCancelled());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFlightInDatabase(Flight flight) {
        String updateFlight = "UPDATE flights SET available_seats = ? WHERE flight_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateFlight)) {
            pstmt.setInt(1, flight.getAvailableSeats());
            pstmt.setString(2, flight.getFlightNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelReservation(Scanner scanner) {
        System.out.print("Enter reservation ID to cancel: ");
        String reservationId = scanner.nextLine();
        Reservation reservationToCancel = null;
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId().equals(reservationId)) {
                reservationToCancel = reservation;
                break;
            }
        }

        if (reservationToCancel == null) {
            System.out.println("Reservation not found.");
            return;
        }

        if (reservationToCancel.isCancelled()) {
            System.out.println("This reservation has already been cancelled.");
            return;
        }

        reservationToCancel.setCancelled(true);
        reservationToCancel.getFlight().setAvailableSeats(reservationToCancel.getFlight().getAvailableSeats() + 1);
        System.out.println("Reservation cancelled successfully.");
    }

    private void viewReservations() {
        System.out.println("Reservations:");
        for (Reservation reservation : reservations) {
            System.out.println("Reservation ID: " + reservation.getReservationId());
            System.out.println("Passenger: " + reservation.getPassenger().getName());
            System.out.println("Flight: " + reservation.getFlight().getFlightNumber() + " (" + reservation.getFlight().getDeparture() + " to " + reservation.getFlight().getDestination() + ")");
            System.out.println("Status: " + (reservation.isCancelled() ? "Cancelled" : "Active"));
            System.out.println("--------------------------------");
        }
    }

    private String generatePassengerId() {
        // Generate a unique passenger ID
        return "P" + (reservations.size() + 1);
    }

    private String generateReservationId() {
        // Generate a unique reservation ID
        return "R" + (reservations.size() + 1);
    }
}
