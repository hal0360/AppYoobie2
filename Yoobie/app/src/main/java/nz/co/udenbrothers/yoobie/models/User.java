package nz.co.udenbrothers.yoobie.models;

/**
 * Created by user on 14/12/2016.
 */
public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String dateOfBirth;
    public int gender;
    public int countryId;
    public int countryRegionId;
    public String deviceToken;
    public String phoneMobile;
    public String deviceModel;
    public String phoneOther;

    public User(){
        firstName = "N/A";
        lastName = "N/A";
        password = "N/A";
        email = "N/A";
        gender = 1;
        dateOfBirth = "N/A";
        deviceToken = "N/A";
        phoneMobile = "N/A";
        phoneOther = "00000000";
        countryId = 1;
        countryRegionId = 1;
        deviceModel = "N/A";
    }
}
