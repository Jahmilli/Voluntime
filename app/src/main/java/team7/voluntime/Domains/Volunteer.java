package team7.voluntime.Domains;

/*

Volunteers:
  <VolunteerID>:
    Profile:
      FullName: String
      Address: String
      PhoneNumber: String
      Gender: String
      DateOfBirth: String
      Email: String
 */
public class Volunteer {
    private String id;
    private String DateOfBirth;
    private String FullName;
    private String Address;
    private String PhoneNumber;
    private String Gender;
    private String Email;

    public Volunteer() {

    }

    public Volunteer(String id, String DateOfBirth, String FullName, String Address, String PhoneNumber, String Gender, String Email) {
        this.id = id;
        this.DateOfBirth = DateOfBirth;
        this.FullName = FullName;
        this.Address = Address;
        this.PhoneNumber = PhoneNumber;
        this.Gender = Gender;
        this.Email = Email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return FullName;
    }

    public String getAddress() {
        return Address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public String getGender() {
        return Gender;
    }

    public String getEmail() {
        return Email;
    }

    @Override
    public String toString() {
        return "Volunteer ID: " + this.getId() +
                "\nName: " + this.getFullName() +
                "\nAddress: " + this.getAddress() +
                "\nPhone Number: " + this.getPhoneNumber() +
                "\nDateOfBirth: " + this.getDateOfBirth() +
                "\nGender: " + this.getGender() +
                "\nEmail: " + this.getEmail();
    }

}
