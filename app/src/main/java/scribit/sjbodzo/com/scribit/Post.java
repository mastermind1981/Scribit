package scribit.sjbodzo.com.scribit;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post implements Parcelable {
    protected int status;
    protected long id;
    protected String title, description;
    protected Double[] location;
    protected String mediaFilePath;
    protected Date postDate;

    public Post(long id, String title, String description,
                Double[] location, String mediaFilePath, Date postDate) {
        status = 0;
        this.title = title;
        this.description = description;
        this.location = location;
        this.mediaFilePath = mediaFilePath;
        this.postDate = postDate;
    }

    Post(Parcel in) {
        String[] strVals = in.createStringArray();
        double[] dAR = in.createDoubleArray();
        title = strVals[0];
        description = strVals[1];
        mediaFilePath = strVals[3];
        status = in.readInt();
        id = in.readLong();
        location = new Double[]{ dAR[0], dAR[1] };
        try { postDate = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse(strVals[2]); }
        catch (ParseException pe) {
            Log.e("DATE FORMAT\t", " = " + postDate.toString());
        }
    }

    static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() { return 0; }
    public void writeToParcel(Parcel destination, int flags) {
        String[] strVals = { title, description, postDate.toString(), mediaFilePath };
        destination.writeStringArray(strVals);
        double[] gpsVals = { location[0].doubleValue(), location[1].doubleValue() };
        destination.writeDoubleArray(gpsVals);
        destination.writeInt(status);
        destination.writeLong(id);
    }
    public void setTitle(String j) { if(!j.isEmpty()) title = j; }
    public void setDescription(String d) { if(!d.isEmpty()) description = d; }
    public void setLocation(Double[] loc) { this.location = loc; }
    public void setLocation(double lat, double lon) {
        if (location == null) { location = new Double[2]; }
        location[0] = lat;
        location[1] = lon;
    }
    public void setImagePath(String s) { mediaFilePath = s; }
    public void setDate(Date d) { postDate = d; }

    public int getStatus() { return status; }
    public long getID() { return id; }
    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public Double[] getLocation() { return location; }
    public String getMediaFilePath() { return mediaFilePath; }
    public Date getDate() { return postDate; }
}
