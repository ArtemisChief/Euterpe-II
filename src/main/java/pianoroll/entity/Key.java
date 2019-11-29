package pianoroll.entity;

import midi.component.MidiPlayer;

import javax.sound.midi.ShortMessage;

public abstract class Key extends GraphicElement{

    private int pitch;

    public Key(int trackID, int colorID) {
        super(trackID,colorID);
        this.pitch = trackID + 21;
    }

    public void press() {
        try {
            ShortMessage shortMessage = new ShortMessage(144, 0, pitch, 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            ShortMessage shortMessage = new ShortMessage(128, 0, pitch, 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
