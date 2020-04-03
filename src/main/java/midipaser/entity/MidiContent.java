package midipaser.entity;

import java.util.ArrayList;
import java.util.List;

public class MidiContent {

    private final int resolution;

    private final List<MidiTrack> midiTrackList;

    public MidiContent(int resolution) {
        this.resolution = resolution;
        this.midiTrackList = new ArrayList<>();
    }

    public int getResolution() {
        return resolution;
    }

    public List<MidiTrack> getMidiTrackList() {
        return midiTrackList;
    }

}
