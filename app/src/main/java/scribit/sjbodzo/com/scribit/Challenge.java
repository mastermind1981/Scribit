package scribit.sjbodzo.com.scribit;

import java.io.File;
import java.util.Date;

interface Challenge {
    public void complete();
}

class ConcreteChallengePost extends Post implements Challenge {

    public ConcreteChallengePost(long id, String title, String description,
                             Double[] location, String postimg, Date postDate) {
        super(id, title, description, location, postimg, postDate);
    }

    public void complete() {
        status = 1; //switches status to complete
        //TODO: notify challengetracker via observer pattern, check if unlocks anything??
    }
}
