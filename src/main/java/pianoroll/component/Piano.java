package pianoroll.component;

import midi.component.MidiPlayer;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;
import pianoroll.util.Semantic;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class Piano {

    private List<Key> keyList;

    private int pitchOffset;

    public Piano() {

        pitchOffset = 0;

        keyList = new ArrayList<>();

        for (int trackID = 0; trackID < Semantic.Piano.KEY_MAX; ++trackID)
            newKey(trackID);

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newKey(int trackID) {
        Key key;
        int vbo;

        if (Key.IsWhite(trackID)) {
            key = new KeyWhite(trackID);
            vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.VERTEX_KEYWHITE);
        } else {
            key = new KeyBlack(trackID);
            vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.VERTEX_KEYBLACK);
        }

        key.setVbo(vbo);
        PianorollCanvas.OfferGraphicElementQueue(key);
        keyList.add(key);
    }

    public void addHalfPitch(){
        pitchOffset++;
    }

    public void reduceHalfPitch(){
        pitchOffset--;
    }

    public List<Key> getKeyList() {
        return keyList;
    }

    public int getPitchOffset() {
        return pitchOffset;
    }

}
