package pianoroll.entity;

import midi.component.MidiPlayer;

import javax.sound.midi.ShortMessage;

public abstract class Key {

    private int keyID;

    private int colorID;

    private int pitch;

    public Key(int keyID, int colorID) {
        this.keyID = keyID;
        this.colorID = colorID;
        this.pitch = keyID + 21;
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

    public int getKeyID() {
        return keyID;
    }

    public int getColorID() {
        return colorID;
    }

    protected void setColorID(int colorID) {
        this.colorID = colorID;
    }

}
