package team7.voluntime.Domains;

public class Event {
    private String id;
    private String title;
    private String description;
    private String category;
    private String location;
    private String date;
    private String createdTime;
    private String organisers;
    private EventVolunteers volunteers;

    public Event(String id, String title, String description, String category, String location, String date, String createdTime, String organisers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.createdTime = createdTime;
        this.organisers = organisers;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getOrganisers() {
        return organisers;
    }

    public void setVolunteers(EventVolunteers volunteers) {
        this.volunteers = volunteers;
    }

    @Override
    public String toString() {
        return "ID: " + this.getId() +
                "\nTitle: " + this.getTitle() +
                "\nDescription: " + this.getDescription() +
                "\nCategory: " + this.getCategory() +
                "\nLocation: " + this.getLocation() +
                "\nDate: " + this.getDate() +
                "\nCreatedTime: " + this.getCreatedTime() +
                "\nOrganisers: " + this.getOrganisers();
    }
}
