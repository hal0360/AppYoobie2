package nz.co.udenbrothers.yoobie.models;

public class LoginData {

    public String email;
    public String password;
    public String deviceToken;
    public String deviceModel;

    public LoginData(String em, String pass, String dm){
        email = em;
        password = pass;
        deviceToken = "N/A";
        deviceModel = dm;
    }
}
