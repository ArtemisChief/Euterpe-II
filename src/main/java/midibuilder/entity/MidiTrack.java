package midibuilder.entity;

public class MidiTrack {

    private byte[] midiTrackData;

    private byte[] midiTrackContentData;

    public MidiTrack() {
        midiTrackData = new byte[0];
        midiTrackContentData = new byte[0];
    }

    public void setMidiTrackData(byte[] midiTrackData) {
        this.midiTrackData = midiTrackData;
    }

    public byte[] getMidiTrackData() {
        return midiTrackData;
    }

    public void setMidiTrackContentData(byte[] midiTrackContentData) {
        this.midiTrackContentData = midiTrackContentData;
    }

    public byte[] getMidiTrackContentData() {
        return midiTrackContentData;
    }

}