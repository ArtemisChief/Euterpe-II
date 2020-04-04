package pianoroll.entity;

import midiplayer.component.MidiPlayer;

import javax.sound.midi.ShortMessage;

public class Key extends GraphicElement {

    private final int pitch;

    public Key(int trackID) {
        super(trackID);

        if (IsWhite(trackID))
            setColorID(200);
        else
            setColorID(201);

        this.pitch = trackID + 21;
    }

    public void press() {
        try {
            ShortMessage shortMessage = new ShortMessage(144, 0, pitch, 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);

            setColorID(getTrackID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            ShortMessage shortMessage = new ShortMessage(128, 0, pitch, 100);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);

            if (IsWhite(getTrackID()))
                setColorID(200);
            else
                setColorID(201);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}