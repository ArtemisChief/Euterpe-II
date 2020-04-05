package pianoroll.component.controller;

import midiplayer.component.MidiPlayer;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Key;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class PianoController {

    private final List<Key> keyList;

    private final List<Integer> triggeredTrackList;
    private final List<Integer> triggeringTrackList;

    private int pitchOffset;

    public PianoController(List<Integer> triggeredTrackList) {
        keyList = new ArrayList<>();

        this.triggeredTrackList = triggeredTrackList;
        triggeringTrackList = new ArrayList<>();

        pitchOffset = 0;

        try {
//            todo sustain
//            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
//            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trigger(int trackID) {
        try {
            Key key = keyList.get(trackID);

            ShortMessage shortMessage = new ShortMessage(144, 0, key.getPitch(), 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);

            key.setColorID(key.getTrackID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void suspend(int trackID) {
        try {
            Key key = keyList.get(trackID);

            ShortMessage shortMessage = new ShortMessage(128, 0, key.getPitch(), 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);

            if (GraphicElement.IsWhite(key.getTrackID()))
                key.setColorID(200);
            else
                key.setColorID(201);
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
