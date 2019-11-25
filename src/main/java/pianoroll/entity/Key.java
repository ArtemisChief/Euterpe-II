package pianoroll.entity;

import midi.component.MidiPlayer;

import javax.sound.midi.ShortMessage;

public abstract class Key {

    private int pitch;

    public Key(int pitch) {
        this.pitch = pitch;
    }

    public int getPitch() {
        return pitch;
    }

    public void press() {
        try {
            ShortMessage shortMessage = new ShortMessage(144, 0, pitch, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            ShortMessage shortMessage = new ShortMessage(128, 0, pitch, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
