package nz.co.udenbrothers.yoobie.models;

/**
 * Created by user on 17/02/2017.
 */

public class Profile {

    public String firstName;
    public String lastName;
    public int gender;
    public String dateOfBirth;
    public String phoneMobile;
    public String phoneOther;

    public Profile(){
        firstName = "N/A";
        lastName = "N/A";
        gender = 1;
        dateOfBirth = "N/A";
        phoneMobile = "N/A";
        phoneOther = "00000000";
    }
}
