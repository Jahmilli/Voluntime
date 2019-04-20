package team7.voluntime.Domains;

import java.util.ArrayList;

public class EventVolunteers {
    private int minimum;
    private int maximum;
    private ArrayList<String> pendingVolunteers;
    private ArrayList<String> registeredVolunteers;
    private ArrayList<String> attendedVolunteers;

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

    public ArrayList<String> getPendingVolunteers() {
        return pendingVolunteers;
    }

    public void setPendingVolunteers(ArrayList<String> pendingVolunteers) {
        this.pendingVolunteers = pendingVolunteers;
    }

    public ArrayList<String> getRegisteredVolunteers() {
        return registeredVolunteers;
    }

    public void setRegisteredVolunteers(ArrayList<String> registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }

    public ArrayList<String> getAttendedVolunteers() {
        return attendedVolunteers;
    }

    public void setAttendedVolunteers(ArrayList<String> attendedVolunteers) {
        this.attendedVolunteers = attendedVolunteers;
    }

    public EventVolunteers() {

    }

    public EventVolunteers(int minimum, int maximum, ArrayList<String> pendingVolunteers, ArrayList<String> registeredVolunteers, ArrayList<String> attendedVolunteers) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.pendingVolunteers = pendingVolunteers;
        this.registeredVolunteers = registeredVolunteers;
        this.attendedVolunteers = attendedVolunteers;
    }

    @Override
    public String toString() {
        return "Minimum: " + minimum +
                "\nMaximum: " + maximum;
//                "\nPending Volunteers: " + pendingVolunteers.toString() +
//                "\nRegistered Volunteers: " + registeredVolunteers.toString() +
//                "\nAttended Volunteers: " + attendedVolunteers.toString();
    }
}
