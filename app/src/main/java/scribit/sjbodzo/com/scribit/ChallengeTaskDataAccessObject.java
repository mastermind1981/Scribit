package scribit.sjbodzo.com.scribit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ChallengeTaskDataAccessObject {

    private ChallengeOpenHelper ChallengeTaskTableHelper; //ref to subclass of SQLiteOpenHelper
    private SQLiteDatabase DBOnDevice; //ref to actual DB on storage of challenge tasks

    public ChallengeTaskDataAccessObject(Context context) {
        ChallengeTaskTableHelper = new ChallengeOpenHelper(context);
    }

    public void open() throws SQLException {
        //method inherited from SQLiteOpenHelper, cached ref once opened initially
        DBOnDevice = ChallengeTaskTableHelper.getWritableDatabase();
    }

    //close necessary after modifications done
    public void close() {
        ChallengeTaskTableHelper.close();
    }

    //Don't allow the user to remove a challenge they didnt create!
    public boolean removeChallengeTask(ChallengeTask challengeTask) {
        if (!challengeTask.getIsUserCreated()) return false;
        long id = challengeTask.getID();
        DBOnDevice.delete(ChallengeOpenHelper.CHALL_TABLE_NAME,
                ChallengeOpenHelper.COLUMN_ID + " = " + id, null);
        return true;
    }

    //creates Challenge Task via Add Challenge Task Wizard page interaction using DB
    public ChallengeTask createChallengeTaskEntry(String title, String desc, int points,
                                                  String unlocked, String mediaFilePath,
                                                  boolean isUserCreated, String category) {
        ContentValues values = new ContentValues();
        values.put(ChallengeTaskTableHelper.COLUMN_TITLE, title);
        values.put(ChallengeTaskTableHelper.COLUMN_DESC, desc);
        values.put(ChallengeTaskTableHelper.COLUMN_POINTS, points);
        values.put(ChallengeTaskTableHelper.COLUMN_UNLOCKED, unlocked);
        values.put(ChallengeTaskTableHelper.COLUMN_IMGPATH, mediaFilePath);
        if (isUserCreated) values.put(ChallengeTaskTableHelper.COLUMN_ISUSERMADE, 1);
        else values.put(ChallengeTaskTableHelper.COLUMN_ISUSERMADE, 0);
        values.put(ChallengeTaskTableHelper.COLUMN_CATEGORY, category);

        //inherited method returns id of location in list
        long insertId = DBOnDevice.insert(ChallengeTaskTableHelper.CHALL_TABLE_NAME, null, values);
        Cursor dbRowCursor = DBOnDevice.query(ChallengeTaskTableHelper.CHALL_TABLE_NAME,
                null, ChallengeTaskTableHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        dbRowCursor.moveToFirst();
        ChallengeTask newChallengeTask = parseCursorRefAsChallengeTask(dbRowCursor);
        dbRowCursor.close();
        return newChallengeTask;
    }

    //helper method for telling how to parse cursor current row as Post obj
    private ChallengeTask parseCursorRefAsChallengeTask(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_TITLE));
        String desc = cursor.getString(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_DESC));
        String filepath = cursor.getString(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_IMGPATH));
        int points = cursor.getInt(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_POINTS));
        String unlockedTitle = cursor.getString(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_UNLOCKED));
        int isUserMadeVal = cursor.getInt(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_ISUSERMADE));
        boolean isUserMade = (isUserMadeVal == 1) ? true : false;
        String category = cursor.getString(cursor.getColumnIndex(ChallengeTaskTableHelper.COLUMN_CATEGORY));

        ChallengeTask cTask = new ChallengeTask(title, points, unlockedTitle,
                                                filepath, isUserMade, category);
        return cTask;
    }

    public List<ChallengeTask> getAllThemThereChallenges() {
        List<ChallengeTask> tasks = new ArrayList<ChallengeTask>();

        //only concern ourselves w/ populating list of visible items for adapter,
        //& then on inspection of individual post redefine projection as needed
        Cursor cursor = DBOnDevice.query(ChallengeOpenHelper.CHALL_TABLE_NAME,
                                         null, null, null, null, null, null);
        //iterate over tuples of Posts, adding to our container at each row
        while(cursor.moveToNext()) {
            ChallengeTask cTask = parseCursorRefAsChallengeTask(cursor);
            tasks.add(cTask);
        }

        cursor.close();
        return tasks;
    }
}
