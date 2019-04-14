package team7.voluntime.Domains;

import java.util.ArrayList;

public class EventVolunteers {
    private int minimum;
    private int maximum;
    private ArrayList<String> pendingVolunteers;
    private ArrayList<String> registeredVolunteers;
    private ArrayList<String> attendedVolunteers;

    public EventVolunteers() {

    }

    public EventVolunteers(int minimum, int maximum, ArrayList<String> pendingVolunteers, ArrayList<String> registeredVolunteers, ArrayList<String> attendedVolunteers) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.pendingVolunteers = pendingVolunteers;
        this.registeredVolunteers = registeredVolunteers;
        this.attendedVolunteers = attendedVolunteers;
    }
}
