package nz.co.udenbrothers.yoobie.models;

/**
 * Created by user on 13/01/2016.
 */
public class Image {
    public int id;
    public String image_name;
    public int renewed_times;
    public String type;

    public Image(int iid, String st, int dur, String tp){
        id = iid;
        image_name = st;
        renewed_times = dur;
        type = tp;
    }
}