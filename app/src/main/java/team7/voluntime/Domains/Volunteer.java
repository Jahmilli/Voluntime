package team7.voluntime.Domains;

/*

Volunteers:
  <VolunteerID>:
    Profile:
      name: String
      address: String
      phoneNumber: String
      gender: String
      dateOfBirth: String
      email: String
 */
public class Volunteer {
    private String id;
    private String dateOfBirth;
    private String name;
    private String address;
    private String phoneNumber;
    private String gender;
    private String email;

    public Volunteer() {

    }

    public Volunteer(String id, String dateOfBirth, String name, String address, String phoneNumber, String gender, String email) {
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.email = email;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Volunteer ID: " + this.getId() +
                "\nName: " + this.getName() +
                "\nAddress: " + this.getAddress() +
                "\nPhone Number: " + this.getPhoneNumber() +
                "\nDateOfBirth: " + this.getDateOfBirth() +
                "\nGender: " + this.getGender() +
                "\nEmail: " + this.getEmail();
    }

}
