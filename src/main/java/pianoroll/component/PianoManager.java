package pianoroll.component;

import midiplayer.component.MidiPlayer;
import pianoroll.entity.Key;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class PianoManager {

    private final List<Key> keyList;

    private int pitchOffset;

    public PianoManager() {
        keyList = new ArrayList<>();

        pitchOffset = 0;

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHalfTone() {
        pitchOffset++;
    }

    public void reduceHalfTone() {
        pitchOffset--;
    }

    public List<Key> getKeyList() {
        return keyList;
    }

    public int getPitchOffset() {
        return pitchOffset;
    }

}
