package foo.bar.example.foreadapters.feature.playlist;

import android.support.annotation.ColorRes;

/**
 *
 */
public class Track {

    @ColorRes
    private final int colourResource;
    private static final int MIN_PLAYS_REQUESTED = 1;
    private static final int MAX_PLAYS_REQUESTED = 9;
    private int numberOfPlaysRequested = MIN_PLAYS_REQUESTED;

    public Track(@ColorRes int colourResource) {
        this.colourResource = colourResource;
    }

    public int getColourResource() {
        return colourResource;
    }

    public int getNumberOfPlaysRequested() {
        return numberOfPlaysRequested;
    }

    // these aren't full observable models,
    // we want the playlist to manage any changes
    // so we don't make this method public
    protected void increasePlaysRequested() {
        if (canIncreasePlays()){
            numberOfPlaysRequested++;
        }
    }

    // these aren't full observable models,
    // we want the playlist to manage any changes
    // so we don't make this method public
    protected void decreasePlaysRequested() {
        if (canDecreasePlays()){
            numberOfPlaysRequested--;
        }
    }

    public boolean canIncreasePlays(){
        return numberOfPlaysRequested<MAX_PLAYS_REQUESTED;
    }

    public boolean canDecreasePlays(){
        return numberOfPlaysRequested>MIN_PLAYS_REQUESTED;
    }
}
