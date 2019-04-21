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
    private EventVolunteers eventVolunteers;

    public Event() {

    }

    public Event(String id, String title, String description, String category, String location, String date, String createdTime, String organisers, EventVolunteers eventVolunteers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.createdTime = createdTime;
        this.organisers = organisers;
        this.eventVolunteers = eventVolunteers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public EventVolunteers getEventVolunteers() {
        return this.eventVolunteers;
    }

    public void setVolunteers(EventVolunteers eventVolunteers) {
        this.eventVolunteers = eventVolunteers;
    }

    @Override
    public String toString() {
        return "ID: " + getId() +
                "\nTitle: " + getTitle() +
                "\nDescription: " + getDescription() +
                "\nCategory: " + getCategory() +
                "\nLocation: " + getLocation() +
                "\nDate: " + getDate() +
                "\nCreatedTime: " + getCreatedTime() +
                "\nOrganisers: " + getOrganisers();
    }
}
