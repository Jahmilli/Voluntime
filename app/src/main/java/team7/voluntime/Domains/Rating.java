package team7.voluntime.Domains;

public class Rating {
    private String ratingId;
    private String charityId;
    private String eventId;
    private float rating;
    private String comment;
    private String dateRated;

    public Rating() {}

    public Rating(String ratingId, String charityId, String eventId, float rating, String comment, String dateRated) {
        this.rating = rating;
        this.charityId = charityId;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
        this.dateRated = dateRated;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getCharityId() {
        return charityId;
    }

    public void setCharityId(String charityId) {
        this.charityId = charityId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateRated() {
        return dateRated;
    }

    public void setDateRated(String dateRated) {
        this.dateRated = dateRated;
    }

    @Override
    public String toString() {
        return "Rating ID: " + this.getRatingId() +
                "\nCharityID: " + this.getCharityId() +
                "\nEventID: " + this.getEventId() +
                "\nRating: " + this.getRating() +
                "\nComment: " + this.getComment() +
                "\nDateRated: " + this.getDateRated();
    }
}
