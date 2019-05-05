package team7.voluntime.Domains;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Volunteer implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(dateOfBirth);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(phoneNumber);
        parcel.writeString(gender);
        parcel.writeString(email);

    }
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods

    public static final Parcelable.Creator<Volunteer> CREATOR = new Parcelable.Creator<Volunteer>() {
        public Volunteer createFromParcel(Parcel in) {
            return new Volunteer(in);
        }

        public Volunteer[] newArray(int size) {
            return new Volunteer[size];
        }
    };

    private Volunteer(Parcel in) {
        id = in.readString();
        dateOfBirth = in.readString();
        name = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        gender = in.readString();
        email = in.readString();

    }
}
