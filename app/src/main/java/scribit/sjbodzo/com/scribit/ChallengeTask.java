package scribit.sjbodzo.com.scribit;

public class ChallengeTask {
    private int ID;
    private String title;
    private int points;
    private String unlocked; // what title did you unlock by completing this challenge?
    private String mediaFilePath; // icon referencing this challenge, use default if none set
    private boolean isUserCreated; // is this a stock challenge or did the user make it?
    private String category; //what cateogory of challenge is this?

    public ChallengeTask() {
        ID = -1;
        title = "A Meaningless Task";
        points = 1;
        unlocked = "Nobody";
        mediaFilePath = ""; //if path never set, default to using default challenge task drawable
        isUserCreated = true;
        category = "";
    }

    public ChallengeTask(String title, int points, String unlocked,
                         String mediaFilePath, boolean isUserCreated,
                         String category) {
        this.title = title;
        this.points = points;
        this.unlocked = unlocked;
        this.mediaFilePath = mediaFilePath;
        this.isUserCreated = isUserCreated;
        this.category = category;
    }

    public String getTitle() { return title; }
    public int getPoints() { return points; }
    public String getUnlocked() { return unlocked; }
    public String getMediaFilePath() { return mediaFilePath; }
    public void setTitle(String t) { title = t; }
    public void setPoints(int i) { points = i; }
    public void setUnlockableTitle(String s) { unlocked = s; }
    public void setMediaResPath(String s) { mediaFilePath = s; }
    public int getID() { return ID; }
    public boolean getIsUserCreated() { return isUserCreated; }
    public String getCategory() { return category; }

    //TODO: consider letting users define own categories of data?
    //TODO: implement Parcelable for showing Challenges in app and viewing them individually
}
