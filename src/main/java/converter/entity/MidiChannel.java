package converter.entity;

import midipaser.entity.MidiEvent;

import java.util.ArrayList;
import java.util.List;

public class MidiChannel {

    private final int channelNumber;

    private final int trackNumber;

    private final List<MidiEvent> midiEventList;

    public MidiChannel(int trackNumber, int channelNumber) {
        this.trackNumber = trackNumber;
        this.channelNumber = channelNumber;
        midiEventList = new ArrayList<>();
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public List<MidiEvent> getMidiEventList() {
        return midiEventList;
    }

}
