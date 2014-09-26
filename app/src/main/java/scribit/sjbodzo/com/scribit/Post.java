package scribit.sjbodzo.com.scribit;

import java.io.File;
import java.util.Date;

public class Post {
    protected int status;
    protected long id;
    protected String title, description;
    protected Double[] location;
    protected File postImage;
    protected Date postDate;

    public Post(long id, String title, String description,
                Double[] location, File postimg, Date postDate) {
        status = 0;
        this.title = title;
        this.description = description;
        this.location = location;
        postImage = postimg;
        this.postDate = postDate;
    }

    public void setTitle(String j) { if(!j.isEmpty()) title = j; }
    public void setDescription(String d) { if(!d.isEmpty()) description = d; }
    public void setLocation(Double[] loc) { this.location = loc; }
    public void setLocation(double lat, double lon) {
        if (location == null) { location = new Double[2]; }
        location[0] = lat;
        location[1] = lon;
    }
    public void setImage(File f) { postImage = f; }
    public void setDate(Date d) { postDate = d; }

    public int getStatus() { return status; }
    public long getID() { return id; }
    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public Double[] getLocation() { return location; }
    public File getImage() { return postImage; }
    public Date getDate() { return postDate; }
}
