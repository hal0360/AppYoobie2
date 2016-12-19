package nz.co.udenbrothers.yoobie.models;

public class Member {

    public int id;
    public String username;
    public String password;
    public String email;
    public String gender;
    public String date_of_birth;
    public String country;
    public String city;
    public String device;
    public String mobile;

    public Member(int ii, String u, String p, String e, String g, String d, String co, String ci, String de, String mo){
        id = ii;
        username = u;
        password = p;
        email = e;
        gender = g;
        date_of_birth = d;
        country = co;
        city = ci;
        device = de;
        mobile = mo;
    }
}
