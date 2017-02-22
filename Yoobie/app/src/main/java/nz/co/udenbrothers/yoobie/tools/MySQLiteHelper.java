package nz.co.udenbrothers.yoobie.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nz.co.udenbrothers.yoobie.models.Image;
import nz.co.udenbrothers.yoobie.models.Image_stamp;


public class MySQLiteHelper extends SQLiteOpenHelper {

    private static MySQLiteHelper sInstance;
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ImageDB";

    public static synchronized MySQLiteHelper getInstance(Context context) {
        if (sInstance == null) sInstance = new MySQLiteHelper(context.getApplicationContext());
        return sInstance;
    }

    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE images ( " +
                "image_id TEXT, " +
                "image_name TEXT, "+
                "renewed_times INTEGER, " +
                "type TEXT )";

        String CREATE_nig_TABLE = "CREATE TABLE image_stamps ( " +
                "image_id INTEGER, "+
                "stamp TEXT )";

        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_nig_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS images");
        db.execSQL("DROP TABLE IF EXISTS image_stamps");
        this.onCreate(db);
    }

    public synchronized void addStamp(String img_id, String stp){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("image_id", img_id);
            values.put("stamp", stp);
            db.insert("image_stamps", null, values);
    }

    public synchronized List<Image_stamp> getStamps() {
        List<Image_stamp> list = new ArrayList<>();
        String query = "SELECT  * FROM image_stamps";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Image_stamp tempStamp = new Image_stamp(cursor.getString(0), cursor.getString(1));
                list.add(tempStamp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized void clearStamp(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("image_stamps", null, null);
        db.close();
    }

    public synchronized void addImage(Image img){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image_id", img.id);
        values.put("image_name", img.image_name);
        values.put("renewed_times", img.renewed_times);
        values.put("type", img.type);
        db.insert("images", null, values);
        db.close();
    }

    public synchronized Image getImage() {
        Image res = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM images", null);

        if(c.getCount() != 0){
            Random rand = new Random();
            int rann = rand.nextInt(c.getCount());
            c.moveToPosition(rann);
            res = new Image(c.getString(0), c.getString(1), c.getInt(2), c.getString(3));
        }
        c.close();
        db.close();
        return res;
    }

    public synchronized void clearImage(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("images", null, null);
        db.close();
    }
}
