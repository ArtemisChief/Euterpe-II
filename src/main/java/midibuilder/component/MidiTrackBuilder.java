package midibuilder.component;

import midibuilder.entity.MidiTrack;
import midibuilder.util.MidiBuilderUtil;

public class MidiTrackBuilder {

    private MidiTrack currentMidiTrack;

    private static final MidiTrackBuilder instance = new MidiTrackBuilder();

    public static MidiTrackBuilder GetInstance() {
        return instance;
    }

    private MidiTrackBuilder() {
        currentMidiTrack = null;
    }

    public MidiTrackBuilder createMidiTrack() {
        currentMidiTrack = new MidiTrack();

        return this;
    }

    public MidiTrack returnCurrentMidiTrack(){
        return currentMidiTrack;
    }

    public MidiTrackBuilder setBpm(float bpm) {
        int microsecondPreNote = MidiBuilderUtil.bpmToMpt(bpm);

        byte[] tempo = new byte[]{0x00, (byte) 0xFF, 0x51, 0x03};
        tempo = MidiBuilderUtil.mergeByte(tempo, MidiBuilderUtil.intToBytes(microsecondPreNote, 3));

        currentMidiTrack.setMidiTrackContentData(MidiBuilderUtil.mergeByte(currentMidiTrack.getMidiTrackContentData(), tempo));

        return this;
    }

    public MidiTrackBuilder setDuration(int duration) {
        if (duration != 0) {
            byte[] midiTrackContentData = currentMidiTrack.getMidiTrackContentData();

            byte[] note = new byte[]{0x00, (byte) 0x90, 0x3C, 0x00};
            midiTrackContentData = MidiBuilderUtil.mergeByte(midiTrackContentData, note);

            note = MidiBuilderUtil.buildBytes(duration);
            midiTrackContentData = MidiBuilderUtil.mergeByte(midiTrackContentData, note);

            note = new byte[]{(byte) 0x80, 0x3C, 0x00};
            midiTrackContentData = MidiBuilderUtil.mergeByte(midiTrackContentData, note);

            currentMidiTrack.setMidiTrackContentData(midiTrackContentData);
        }

        return this;
    }

    public MidiTrackBuilder addController(byte channel, byte type, byte param) {
        byte[] controller = new byte[]{0x00, (byte) (0xB0 + channel), type, param};
        currentMidiTrack.setMidiTrackContentData(MidiBuilderUtil.mergeByte(currentMidiTrack.getMidiTrackContentData(), controller));

        return this;
    }

    public MidiTrackBuilder setInstrument(byte channel, byte type) {
        byte[] instrument = new byte[]{0x00, (byte) (0xC0 + channel), type};
        currentMidiTrack.setMidiTrackContentData(MidiBuilderUtil.mergeByte(currentMidiTrack.getMidiTrackContentData(), instrument));

        return this;
    }

    public MidiTrackBuilder insertNoteOff(int deltaTime, byte channel, byte note) {
        byte[] midiTrackContentData = currentMidiTrack.getMidiTrackContentData();

        midiTrackContentData = MidiBuilderUtil.mergeByte(midiTrackContentData, MidiBuilderUtil.buildBytes(deltaTime));

        byte[] noteOff = new byte[]{(byte) (0x80 + channel), note, 0x00};

        midiTrackContentData = MidiBuilderUtil.mergeByte(midiTrackContentData, noteOff);

        currentMidiTrack.setMidiTrackContentData(midiTrackContentData);

        return this;
    }

    public MidiTrackBuilder insertNoteOn(byte channel, byte note, byte velocity) {
        byte[] noteOn;

        if (note != 0)
            noteOn = new byte[]{0x00, (byte) (0x90 + channel), note, velocity};
        else
            noteOn = new byte[]{0x00, (byte) (0x90 + channel), note, 0x00};

        currentMidiTrack.setMidiTrackContentData(MidiBuilderUtil.mergeByte(currentMidiTrack.getMidiTrackContentData(), noteOn));

        return this;
    }

    public MidiTrackBuilder setStart() {
        currentMidiTrack.setMidiTrackData(new byte[]{0x4D, 0x54, 0x72, 0x6B});

        return this;
    }

    public MidiTrackBuilder setEnd(MidiTrack midiTrack) {
        byte[] midiTrackData = midiTrack.getMidiTrackData();
        byte[] midiTrackContentData = midiTrack.getMidiTrackContentData();

        midiTrackData = MidiBuilderUtil.mergeByte(midiTrackData, MidiBuilderUtil.intToBytes(midiTrackContentData.length + 4, 4));
        midiTrackData = MidiBuilderUtil.mergeByte(midiTrackData, midiTrackContentData);
        midiTrackData = MidiBuilderUtil.mergeByte(midiTrackData, new byte[]{0x00, (byte) 0xFF, 0x2F, 0x00});

        midiTrack.setMidiTrackData(midiTrackData);

        return this;
    }

    public MidiTrackBuilder merge(MidiTrack midiTrack1, MidiTrack midiTrack2) {
        midiTrack1.setMidiTrackContentData(MidiBuilderUtil.mergeByte(midiTrack1.getMidiTrackContentData(), midiTrack2.getMidiTrackContentData()));

        return this;
    }

}
