package midipaser.entity;

import java.util.ArrayList;
import java.util.List;

public class MidiContent {

    private final int resolution;

    private final long tickLength;

    private final List<MidiTrack> midiTrackList;

    public MidiContent(int resolution, long tickLength) {
        this.resolution = resolution;
        this.tickLength = tickLength;
        this.midiTrackList = new ArrayList<>();
    }

    public int getResolution() {
        return resolution;
    }

    public long getTickLength() {
        return tickLength;
    }

    public List<MidiTrack> getMidiTrackList() {
        return midiTrackList;
    }

}
