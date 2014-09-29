package scribit.sjbodzo.com.scribit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsDataAccessObject {

    private PostOpenHelper PostTableHelper; //ref to subclass of SQLiteOpenHelper
    private SQLiteDatabase DBOnDevice; //ref to actual DB on storage of posts
    //ref to columns visible, list defined in CustomPostAdapter implementation
    private String[] visibleColumns = {
            PostTableHelper.COLUMN_ID,
            PostTableHelper.COLUMN_DATE,
            PostTableHelper.COLUMN_TITLE
    };

    public PostsDataAccessObject(Context context) {
        PostTableHelper = new PostOpenHelper(context);
    }

    public void open() throws SQLException {
        //method inherited from SQLiteOpenHelper, cached ref once opened initially
        DBOnDevice = PostTableHelper.getWritableDatabase();
    }

    //close necessary after modifications done
    public void close() {
        PostTableHelper.close();
    }

    public void removePost(Post posta) {
        long id = posta.getID();
        DBOnDevice.delete(PostOpenHelper.POSTS_TABLE_NAME,
                PostOpenHelper.COLUMN_ID + " = " + id, null);
    }

    //creates Post object via wizard interaction in AddJournalEntry within DB
    public Post createJournalPost(String title, String desc, double gps_x,
                                  double gps_y, String imgpath, String date) {
        //TODO: input validation before attempting write on DB (i.e: for non null fields)
        //this includes primitives that CANNOT be null ^ 
        ContentValues values = new ContentValues();
        values.put(PostTableHelper.COLUMN_TITLE, title);
        values.put(PostTableHelper.COLUMN_DESC, desc);
        values.put(PostTableHelper.COLUMN_LOC_X, gps_x);
        values.put(PostTableHelper.COLUMN_LOC_Y, gps_y);
        values.put(PostTableHelper.COLUMN_IMGPATH, imgpath);
        values.put(PostTableHelper.COLUMN_DATE, date);

        //inherited method returns id of location in list
        long insertId = DBOnDevice.insert(PostTableHelper.POSTS_TABLE_NAME, null, values);
        Log.e("ID TABLE COLUMN VALUE:\t", insertId + " " + PostTableHelper.COLUMN_ID + " = " + insertId);
        Cursor dbRowCursor = DBOnDevice.query(PostTableHelper.POSTS_TABLE_NAME,
                                              null, PostTableHelper.COLUMN_ID + " = " + insertId,
                                              null, null, null, null);
        //move cursor onto new entry (at top of list), parse cursor current row, & close cursor
        dbRowCursor.moveToFirst();
        Post theNewPost = parseCursorRefAsPost(dbRowCursor);
        dbRowCursor.close();
        return theNewPost;
    }

    //helper method for telling how to parse cursor current row as Post obj
    private Post parseCursorRefAsPost(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(PostTableHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndex(PostTableHelper.COLUMN_TITLE));
        String desc = cursor.getString(cursor.getColumnIndex(PostTableHelper.COLUMN_DESC));
        double locx = cursor.getDouble(cursor.getColumnIndex(PostTableHelper.COLUMN_LOC_X));
        double locy = cursor.getDouble(cursor.getColumnIndex(PostTableHelper.COLUMN_LOC_Y));
        String filepath = cursor.getString(cursor.getColumnIndex(PostTableHelper.COLUMN_IMGPATH));
        String date = cursor.getString(cursor.getColumnIndex(PostTableHelper.COLUMN_DATE));
        Double[] locs = new Double[] {locx, locy};

        Date d = null; //date CANNOT stay null!
        try { d = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date); }
        catch (ParseException pe) { pe.toString(); }
        Post postyPoo = new Post(id, title, desc, locs, new File(filepath), d);

        return postyPoo;
    }

    public List<Post> getAllOfThePostyThings() {
        List<Post> posties = new ArrayList<Post>();

        //only concern ourselves w/ populating list of visible items for adapter,
        //& then on inspection of individual post redefine projection as needed
        Cursor cursor = DBOnDevice.query(PostOpenHelper.POSTS_TABLE_NAME,
                                         null, null, null, null, null, null);
        //iterate over tuples of Posts, adding to our container at each row
        while(cursor.moveToNext()) {
            Post pasta = parseCursorRefAsPost(cursor);
            posties.add(pasta);
        }

        cursor.close();
        return posties;
    }
}
