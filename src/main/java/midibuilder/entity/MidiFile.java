package midibuilder.entity;

import midibuilder.util.MidiBuilderUtil;
import java.util.List;

public class MidiFile {

    private byte[] midiFileData;

    private final List<MidiTrack> midiTracks;

    public MidiFile(List<MidiTrack> midiTracks) {
        midiFileData = new byte[0];
        this.midiTracks = midiTracks;
    }

    public void setMidiFileData(byte[] midiFileData) {
        this.midiFileData = midiFileData;
    }

    public byte[] getMidiFileData() {
        return midiFileData;
    }

    public List<MidiTrack> getMidiTracks() {
        return midiTracks;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(MidiBuilderUtil.bytesToHex(midiFileData));

        for (int i = 138; i < stringBuilder.length(); i += 139)
            stringBuilder.replace(i, i, "\n");



        return stringBuilder.toString();
    }

}