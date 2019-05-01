package team7.voluntime.Domains;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Add Event Time!!
public class Event implements Parcelable {
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(category);
        parcel.writeString(location);
        parcel.writeString(date);
        parcel.writeString(organisers);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        category = in.readString();
        location = in.readString();
        date = in.readString();
        createdTime = in.readString();
        organisers = in.readString();
        // TODO: Add Event Volunteers here

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
