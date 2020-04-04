package midiplayer.component;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import java.io.File;

public class MidiPlayer {

    private static final MidiPlayer instance = new MidiPlayer();

    public static MidiPlayer GetInstance() {
        return instance;
    }

    private Synthesizer synthesizer;

    private Sequencer sequencer;

    private Soundbank soundbank;

    private long microsecondPosition;

    private boolean isLoadedMidiFile;

    private MidiPlayer() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());

            isLoadedMidiFile = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSoundBank(File soundFontFile) {
        try {
            if (soundbank != null)
                synthesizer.unloadAllInstruments(soundbank);

            synthesizer.loadAllInstruments(soundbank = MidiSystem.getSoundbank(soundFontFile));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMidiFile(File midiFile) {
        try {
            sequencer.setSequence(MidiSystem.getSequence(midiFile));
            microsecondPosition = 0;
            isLoadedMidiFile = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getIsLoadedMidiFile() {
        return isLoadedMidiFile;
    }

    public void play() {
        sequencer.setMicrosecondPosition(microsecondPosition);
        sequencer.start();
    }

    public void pause() {
        microsecondPosition = sequencer.getMicrosecondPosition();
        sequencer.stop();
    }

    public void stop() {
        microsecondPosition = 0;
        sequencer.stop();
        isLoadedMidiFile = false;
    }

    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public void close() {
        sequencer.close();
        synthesizer.close();
    }

}