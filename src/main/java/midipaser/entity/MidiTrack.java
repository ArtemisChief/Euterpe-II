package midipaser.entity;

import java.util.ArrayList;
import java.util.List;

public class MidiTrack {

    private final int trackNumber;

    private final List<MidiEvent> midiEventList;

    public MidiTrack(int trackNumber) {
        this.trackNumber = trackNumber;
        midiEventList = new ArrayList<>();
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public List<MidiEvent> getMidiEventList() {
        return midiEventList;
    }

}
