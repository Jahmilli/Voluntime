package team7.voluntime.Domains;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Event implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String category;
    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private String createdTime;
    private String organisers;
    private int minimum;
    private int maximum;
    private HashMap<String, String> volunteers;

    // Fields not in Firebase (and shouldn't be)
    private boolean isPastEvent;
    private String volunteerStatus;

    public Event() {

    }

    public Event(String id, String title, String description, String category, String location, String date, String startTime, String endTime, String createdTime, String organisers, int minimum, int maximum, HashMap<String, String> volunteers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdTime = createdTime;
        this.organisers = organisers;
        this.minimum = minimum;
        this.maximum = maximum;
        this.volunteers = volunteers;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getOrganisers() {
        return organisers;
    }


    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public HashMap<String, String> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(HashMap<String, String> volunteers) {
        this.volunteers = volunteers;
    }

    public boolean isPastEvent() {
        return isPastEvent;
    }

    public void setPastEvent(boolean pastEvent) {
        isPastEvent = pastEvent;
    }

    public String getVolunteerStatus() {
        return volunteerStatus;
    }

    public void setVolunteerStatus(String volunteerStatus) {
        this.volunteerStatus = volunteerStatus;
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
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(createdTime);
        parcel.writeString(organisers);
        parcel.writeInt(minimum);
        parcel.writeInt(maximum);
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

    private Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        category = in.readString();
        location = in.readString();
        date = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        createdTime = in.readString();
        organisers = in.readString();
        minimum = in.readInt();
        maximum = in.readInt();
    }

    @Override
    public String toString() {
        return "ID: " + getId() +
                "\nTitle: " + getTitle() +
                "\nDescription: " + getDescription() +
                "\nCategory: " + getCategory() +
                "\nLocation: " + getLocation() +
                "\nDate: " + getDate() +
                "\nStart Time: " + getStartTime() +
                "\nEnd Time: " + getEndTime() +
                "\nCreatedTime: " + getCreatedTime() +
                "\nOrganisers: " + getOrganisers() +
                "\nMinimum: " + getMinimum() +
                "\nMaximum: " + getMaximum();
    }
}
