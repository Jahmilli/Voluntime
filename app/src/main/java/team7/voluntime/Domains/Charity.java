package team7.voluntime.Domains;

/*

Charities:
  <CharityID>:
    Profile:
      Name: String
      Address: String
      PhoneNumber: String
      Description: String
      Category: [String]
    Events:
      Upcoming: [EventID]
      Previous: [EventID]
 */
public class Charity {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private String category;

    public Charity() {

    }

    public Charity(String id, String name, String address, String phoneNumber, String description, String category) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Charity ID: " + this.getId() +
                "\nCharity Name: " + this.getName() +
                "\nAddress: " + this.getAddress() +
                "\nPhone Number: " + this.getPhoneNumber() +
                "\nDescription: " + this.getDescription() +
                "\nCategory: " + this.getCategory();
    }

}
