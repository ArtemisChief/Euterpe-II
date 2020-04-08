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

    private int pitchOffset;

    public PianoController(List<Integer> triggeredTrackList) {
        keyList = new ArrayList<>();

        this.triggeredTrackList = triggeredTrackList;

        pitchOffset = 0;

//        try {
//            //todo sustain
//            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
//            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void trigger(int trackID) {
        try {
            ShortMessage shortMessage = new ShortMessage(144, 0, keyList.get(trackID).getPitch(), 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void suspend(int trackID) {
        try {
            ShortMessage shortMessage = new ShortMessage(128, 0, keyList.get(trackID).getPitch(), 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateKeys() {
        for (Key key : keyList) {
            if (GraphicElement.IsWhite(key.getTrackID()))
                key.setColorID(200);
            else
                key.setColorID(201);
        }

        for (int trackID : triggeredTrackList) {
            Key key = keyList.get(trackID);
            key.setColorID(key.getTrackID() + 100);
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
