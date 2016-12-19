package nz.co.udenbrothers.yoobie.models;

/**
 * Created by user on 14/12/2016.
 */
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
    private int gender;
    private String deviceToken;
    private String phoneMobile;

    public User(){
        firstName = "Xie2";
        lastName = "Dong2";
        password = "helloworld222";
        email = "dummy2@gmail.com";
        gender = 1;
        dateOfBirth = "1960-11-11";
        deviceToken = "sffgshfuishfushfusdf";
        phoneMobile = "+64271114567";
    }
}
