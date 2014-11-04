package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChallengeOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String CHALL_TABLE_NAME = "challenges";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STATUS = "_status"; //1 == completed, 0 == not done
    public static final String COLUMN_TITLE = "_title"; //what the challenge is called
    public static final String COLUMN_DESC = "_desc"; //what the challenge requires
    public static final String COLUMN_IMGPATH = "_imgpath"; //file ref to icon associated w/ challenge
    public static final String COLUMN_POINTS = "_points"; //how many points you earn for unlocking this challenge
    public static final String COLUMN_UNLOCKED = "_unlocked"; //what title did you unlock?
    public static final String COLUMN_ISUSERMADE = "_usergen"; //is this a stock task or did the user create it?
    public static final String COLUMN_CATEGORY = "_category"; //what category does this challenge belong to
    public static final String COLUMN_LAT = "_lat"; //latitude of where challenge is to complete
    public static final String COLUMN_LONG = "_long"; //longitude of where challenge is to complete

    private static final String CHALL_TABLE_CREATE_STMNT =
            "CREATE TABLE " + CHALL_TABLE_NAME +
                    "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DESC + " TEXT NOT NULL, " +
                    COLUMN_IMGPATH + " TEXT, " +
                    COLUMN_POINTS + " INTEGER NOT NULL, " +
                    COLUMN_ISUSERMADE + " INTEGER NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_STATUS + " INTEGER NOT NULL, " +
                    COLUMN_LAT + " REAL NOT NULL, " +
                    COLUMN_LONG + " REAL NOT NULL, " +
                    COLUMN_UNLOCKED + " TEXT NOT NULL" +
                    ");";

    ChallengeOpenHelper(Context context) {
        super(context, CHALL_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CHALL_TABLE_CREATE_STMNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int alpha, int beta) {
        //log removal of old table
        Log.d(PostOpenHelper.class.getName(),
                "Upgrading database from version " + alpha + " to "
                + beta + ", which will destroy all old data");
        //drop old table
        db.execSQL("DROP TABLE IF EXISTS " + CHALL_TABLE_NAME);
        //re-init new table
        onCreate(db);
    }
}