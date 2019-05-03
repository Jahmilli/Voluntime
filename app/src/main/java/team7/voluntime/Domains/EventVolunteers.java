package team7.voluntime.Domains;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class EventVolunteers { //implements Parcelable
    private int minimum;
    private int maximum;
    private HashMap<String, String> volunteers;

    public EventVolunteers() {

    }

    // example constructor that takes a Parcel and gives you an object populated with it's values
//    private EventVolunteers(Parcel in) {
//        minimum = in.readInt();
//        maximum = in.readInt();
//    }

    public EventVolunteers(int minimum, int maximum, HashMap<String, String> volunteers) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.volunteers = volunteers;
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
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(minimum);
//        parcel.writeInt(maximum);
//    }

//    public static final Parcelable.Creator<EventVolunteers> CREATOR = new Parcelable.Creator<EventVolunteers>() {
//        public EventVolunteers createFromParcel(Parcel in) {
//            return new EventVolunteers(in);
//        }
//
//        public EventVolunteers[] newArray(int size) {
//            return new EventVolunteers[size];
//        }
//    };

    @Override
    public String toString() {
        return "Minimum: " + minimum +
                "\nMaximum: " + maximum +
                "\nVolunteers: " + volunteers.toString();
    }
}
