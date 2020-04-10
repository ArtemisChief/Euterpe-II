package pianoroll.component.controller;

import midiplayer.component.MidiPlayer;
import pianoroll.component.Pianoroll;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Key;
import pianoroll.util.Semantic;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class PianoController {

    private final List<Key> keyList;

    private int pitchOffset;

    public PianoController() {
        keyList = new ArrayList<>();

        pitchOffset = 0;

//        try {
//            //todo sustain
//            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
//            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void pressKey(int trackID) {
        try {
            ShortMessage shortMessage = new ShortMessage(144, 0, keyList.get(trackID).getPitch(), 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseKey(int trackID) {
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
                key.setColorID(Semantic.Color.WHITE);
            else
                key.setColorID(Semantic.Color.BLACK);
        }

        for (int trackID : Pianoroll.GetInstance().getTriggeredTrackList()) {
            Key key = keyList.get(trackID);
            key.setColorID(key.getTrackID());
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
