package nz.co.udenbrothers.yoobie.models;

/**
 * Created by user on 13/02/2017.
 */

public class Response {
    public String content;
    public int statusCode;

    public Response(String c, int sc){
        content = c;
        statusCode = sc;
    }
}
