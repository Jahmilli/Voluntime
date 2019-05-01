package team7.voluntime.Domains;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;

public class EventVolunteers implements Parcelable {
    private int minimum;
    private int maximum;
    private LinkedList<String> pendingVolunteers;
    private LinkedList<String> registeredVolunteers;
    private LinkedList<String> attendedVolunteers;

    public EventVolunteers() {

    }

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private EventVolunteers(Parcel in) {
        pendingVolunteers = new LinkedList<>();
        registeredVolunteers = new LinkedList<>();
        attendedVolunteers = new LinkedList<>();
        minimum = in.readInt();
        maximum = in.readInt();
        in.readStringList(pendingVolunteers);
        in.readStringList(registeredVolunteers);
        in.readStringList(attendedVolunteers);
    }

    public EventVolunteers(int minimum, int maximum, LinkedList<String> pendingVolunteers, LinkedList<String> registeredVolunteers, LinkedList<String> attendedVolunteers) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.pendingVolunteers = pendingVolunteers;
        this.registeredVolunteers = registeredVolunteers;
        this.attendedVolunteers = attendedVolunteers;
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

    public LinkedList<String> getPendingVolunteers() {
        return pendingVolunteers;
    }

    public void setPendingVolunteers(LinkedList<String> pendingVolunteers) {
        this.pendingVolunteers = pendingVolunteers;
    }

    public LinkedList<String> getRegisteredVolunteers() {
        return registeredVolunteers;
    }

    public void setRegisteredVolunteers(LinkedList<String> registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }

    public LinkedList<String> getAttendedVolunteers() {
        return attendedVolunteers;
    }

    public void setAttendedVolunteers(LinkedList<String> attendedVolunteers) {
        this.attendedVolunteers = attendedVolunteers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(minimum);
        parcel.writeInt(maximum);
        parcel.writeStringList(pendingVolunteers);
        parcel.writeStringList(registeredVolunteers);
        parcel.writeStringList(attendedVolunteers);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<EventVolunteers> CREATOR = new Parcelable.Creator<EventVolunteers>() {
        public EventVolunteers createFromParcel(Parcel in) {
            return new EventVolunteers(in);
        }

        public EventVolunteers[] newArray(int size) {
            return new EventVolunteers[size];
        }
    };

    @Override
    public String toString() {
        return "Minimum: " + minimum +
                "\nMaximum: " + maximum +
                "\nPending Volunteers: " + pendingVolunteers.toString() +
                "\nRegistered Volunteers: " + registeredVolunteers.toString() +
                "\nAttended Volunteers: " + attendedVolunteers.toString();
    }
}
