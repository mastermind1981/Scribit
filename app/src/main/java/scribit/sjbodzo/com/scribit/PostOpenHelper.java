package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PostOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String POSTS_TABLE_NAME = "posts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "_title";
    public static final String COLUMN_DESC = "_desc";
    public static final String COLUMN_LOC_X = "_loc_x";
    public static final String COLUMN_LOC_Y = "_loc_y";
    public static final String COLUMN_IMGPATH = "_imgpath";
    public static final String COLUMN_DATE = "_date";

    private static final String POSTS_TABLE_CREATE_STMNT =
            "CREATE TABLE " + POSTS_TABLE_NAME +
            "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DESC + " TEXT NOT NULL, " +
                COLUMN_LOC_X + " REAL, " +
                COLUMN_LOC_Y + " REAL, " +
                COLUMN_IMGPATH + " TEXT, " +
                COLUMN_DATE + " TEXT NOT NULL" +
            ");";

    PostOpenHelper(Context context) {
        super(context, POSTS_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POSTS_TABLE_CREATE_STMNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int alpha, int beta) {
        //log removal of old table
        Log.d(PostOpenHelper.class.getName(),
                "Upgrading database from version " + alpha + " to "
                        + beta + ", which will destroy all old data");
        //drop old table
        db.execSQL("DROP TABLE IF EXISTS " + POSTS_TABLE_NAME);
        //re-init new table
        onCreate(db);
    }
}