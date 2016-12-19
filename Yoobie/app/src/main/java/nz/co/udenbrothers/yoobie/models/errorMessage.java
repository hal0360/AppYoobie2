package nz.co.udenbrothers.yoobie.models;

/**
 * Created by user on 12/02/2016.
 */
public class errorMessage {
    public int user_id;
    public String stamp;
    public String content;

    public errorMessage(int mid, String st, String dur){
        user_id = mid;
        stamp = st;
        content = dur;
    }
}
