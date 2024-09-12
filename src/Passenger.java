public class Passenger {
    private String passengerId;
    private String name;
    private String contactInfo;

    public Passenger(String passengerId, String name, String contactInfo) {
        this.passengerId = passengerId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    // Getters and Setters
    public String getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }
}
