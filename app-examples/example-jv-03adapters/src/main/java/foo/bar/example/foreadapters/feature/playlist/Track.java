package foo.bar.example.foreadapters.feature.playlist;

import androidx.annotation.ColorRes;

import java.util.Random;

import co.early.fore.adapters.immutable.DeepCopyable;
import co.early.fore.adapters.immutable.DiffComparator;

public class Track implements DeepCopyable<Track>, DiffComparator<Track> {

    private static Random random = new Random();

    @ColorRes
    private final int colourResource;
    private static final int MIN_PLAYS_REQUESTED = 1;
    public static final int MAX_PLAYS_REQUESTED = 4;
    private int numberOfPlaysRequested = MIN_PLAYS_REQUESTED;
    private long uniqueId = random.nextLong();

    public Track(@ColorRes int colourResource) {
        this.colourResource = colourResource;
    }

    public int getColourResource() {
        return colourResource;
    }

    public int getNumberOfPlaysRequested() {
        return numberOfPlaysRequested;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    // these aren't full observable models,
    // we want the playlist to manage any changes
    // so we don't make this method public
    protected void increasePlaysRequested() {
        if (canIncreasePlays()) {
            numberOfPlaysRequested++;
        }
    }

    // these aren't full observable models,
    // we want the playlist to manage any changes
    // so we don't make this method public
    protected void decreasePlaysRequested() {
        if (canDecreasePlays()) {
            numberOfPlaysRequested--;
        }
    }

    public boolean canIncreasePlays() {
        return numberOfPlaysRequested < MAX_PLAYS_REQUESTED;
    }

    public boolean canDecreasePlays() {
        return numberOfPlaysRequested > MIN_PLAYS_REQUESTED;
    }

    @Override
    public Track deepCopy() {
        Track copyTrack = new Track(getColourResource());
        copyTrack.numberOfPlaysRequested = numberOfPlaysRequested;
        copyTrack.uniqueId = uniqueId;
        return copyTrack;
    }

    @Override
    public boolean itemsTheSame(Track other) {
        return other != null && other.uniqueId == uniqueId;
    }

    @Override
    public boolean itemsLookTheSame(Track other) {
        return other != null &&
                other.numberOfPlaysRequested == numberOfPlaysRequested &&
                other.colourResource == colourResource;
    }
}
