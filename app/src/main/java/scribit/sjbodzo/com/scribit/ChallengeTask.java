package scribit.sjbodzo.com.scribit;

import android.os.Parcel;
import android.os.Parcelable;

public class ChallengeTask implements Parcelable {
    private long ID;
    private String title;
    private String description;
    private int points;
    private int status; //0 if not done, 1 if completed
    private String unlocked; // what title did you unlock by completing this challenge?
    private String mediaFilePath; // icon referencing this challenge, use default if none set
    private boolean isUserCreated; // is this a stock challenge or did the user make it?
    private String category; //what category of challenge is this?

    public ChallengeTask() {
        ID = -1;
        title = "A Meaningless Task";
        points = 1;
        unlocked = "Nobody";
        mediaFilePath = ""; //if path never set, default to using default challenge task drawable
        isUserCreated = true;
        category = "";
        status = 0;
    }

    public ChallengeTask(long id, String title, int points, String unlocked,
                         String mediaFilePath, boolean isUserCreated,
                         String category, String description) {
        ID = id;
        this.title = title;
        this.points = points;
        this.unlocked = unlocked;
        this.mediaFilePath = mediaFilePath;
        this.isUserCreated = isUserCreated;
        this.category = category;
        this.description = description;
        status = 0;
    }

    //Implementation of Parcelable for viewing ChallengeTasks (pass them from view to fragment to view)
    @Override
    public int describeContents() { return 0; }
    ChallengeTask(Parcel in) {
        String[] strVals = in.createStringArray();
        title = strVals[0];
        unlocked = strVals[1];
        mediaFilePath = strVals[2];
        category = strVals[3];
        description = strVals[4];

        int[] intVals = in.createIntArray();
        points = intVals[0];
        status = intVals[1];
        isUserCreated = (intVals[2] == 1) ? true : false;
        ID = in.readLong();
    }
    public void writeToParcel(Parcel destination, int flags) {
        int[] intVals = { points, status, 1 };
        intVals[2] = (isUserCreated) ? 1 : 0;
        String[] strVals = { title, unlocked, mediaFilePath, category, description };
        destination.writeIntArray(intVals);
        destination.writeStringArray(strVals);
        destination.writeLong(ID);
    }
    public static final Parcelable.Creator<ChallengeTask> CREATOR = new Parcelable.Creator<ChallengeTask>() {
        public ChallengeTask createFromParcel(Parcel in) { return new ChallengeTask(in); }
        public ChallengeTask[] newArray(int size) { return new ChallengeTask[size]; }
    };


    public String getTitle() { return title; }
    public int getPoints() { return points; }
    public String getUnlocked() { return unlocked; }
    public String getMediaFilePath() { return mediaFilePath; }
    public void setTitle(String t) { title = t; }
    public void setPoints(int i) { points = i; }
    public void setUnlockableTitle(String s) { unlocked = s; }
    public void setMediaResPath(String s) { mediaFilePath = s; }
    public long getID() { return ID; }
    public boolean getIsUserCreated() { return isUserCreated; }
    public String getCategory() { return category; }
    public void setStatus(int i) { status = i; }
    public int getStatus() { return status; }

    //TODO: consider letting users define own categories of data?
    //TODO: implement Parcelable for showing Challenges in app and viewing them individually
}
