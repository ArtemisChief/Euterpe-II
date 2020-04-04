package midibuilder.component;

import midibuilder.entity.MidiFile;
import midibuilder.entity.MidiTrack;
import midibuilder.util.MidiBuilderUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MidiFileBuilder {

    private MidiFile currentMidiFile;

    private static final MidiFileBuilder instance = new MidiFileBuilder();

    public static MidiFileBuilder GetInstance() {
        return instance;
    }

    private MidiFileBuilder() {
        currentMidiFile = null;
    }

    public MidiFileBuilder createMidiFile(List<MidiTrack> trackList) {
        currentMidiFile = new MidiFile(trackList);

        return this;
    }

    public MidiFile getCurrentMidiFile() {
        return currentMidiFile;
    }

    public void constructMidiFile() {

        byte[] midiFileData = new byte[]{0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x01};

        int trackCount = currentMidiFile.getMidiTracks().size();
        midiFileData = MidiBuilderUtil.mergeByte(midiFileData, MidiBuilderUtil.intToBytes(trackCount, 2));
        midiFileData = MidiBuilderUtil.mergeByte(midiFileData, new byte[]{0x00, 0x78});

        for (MidiTrack midiTrack : currentMidiFile.getMidiTracks()) {
            midiFileData = MidiBuilderUtil.mergeByte(midiFileData, midiTrack.getMidiTrackData());
        }

        currentMidiFile.setMidiFileData(midiFileData);

    }

    public boolean writeToFile(MidiFile midiFile, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(midiFile.getMidiFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
