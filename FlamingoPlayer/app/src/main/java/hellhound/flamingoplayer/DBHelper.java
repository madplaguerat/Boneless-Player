package hellhound.flamingoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private final static String TAG = "db debug";

    private final static String DB_NAME = "flamingo.db";
    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_ARTISTS = "artists";
    private final static String KEY_ARTIST_NAME = "artists_name";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createArtists = "CREATE TABLE " + TABLE_ARTISTS + "(" +
                KEY_ARTIST_NAME + " TEXT NOT NULL)";
        db.execSQL(createArtists);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);

        onCreate(db);
    }


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------------ Artist methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public long addArtist(ArtistItem item){

        long artist_id = artistExists(item, false);

        if(artist_id == -1){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ARTIST_NAME, item.getName());

            artist_id = db.insert(TABLE_ARTISTS, null, values);
            Log.i(TAG, "Put artist, rowid = " + String.valueOf(artist_id));
            db.close();
        }
        Log.i(TAG, item.getName() + " returns " + String.valueOf(artist_id));
        return artist_id;
    }

    public int artistExists(ArtistItem item, boolean updateFound){
        Log.i(TAG, "Searching for " + item. getName());
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT rowid FROM " + TABLE_ARTISTS +
                " WHERE \"" + item.getName() + "\" = " + KEY_ARTIST_NAME + ";";
        Cursor c = db.rawQuery(query, null);

        int res = -1;
        if(c.moveToFirst()){
            res = c.getInt(c.getColumnIndex("rowid"));
            Log.i(TAG, item.getName() + " found at " + String.valueOf(res));
        }
        c.close();
        db.close();
        Log.i(TAG, "Artist found at position " + String.valueOf(res));
        return res;
    }

    public ArrayList<MenuItem> getAllArtists(){
        ArrayList<MenuItem> res = new ArrayList<MenuItem>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ARTISTS + " ORDER BY " +
                KEY_ARTIST_NAME + " ASC;";

        Cursor c = db.rawQuery(query, null);
        Log.i(TAG, "Created cursor");

        if(c != null){
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Log.i(TAG, "Added " + c.getString(c.getColumnIndex(KEY_ARTIST_NAME)));
                MenuItem item = new ArtistItem(c.getString(c.getColumnIndex(KEY_ARTIST_NAME)));
                res.add(item);
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        return res;
    }
}
