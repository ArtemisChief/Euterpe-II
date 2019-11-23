package component.midi;

import javax.sound.midi.ShortMessage;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MidiPiano {

    private static MidiPiano instance = new MidiPiano();

    public static MidiPiano GetInstance() {
        return instance;
    }

    private MidiPiano() {
        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int parseNote(KeyEvent keyEvent){
        switch (keyEvent.getKeyCode()){
            case KeyEvent.VK_LEFT:
                return 48;
            case KeyEvent.VK_DOWN:
                return 50;
            case KeyEvent.VK_RIGHT:
                return 52;
            case KeyEvent.VK_UP:
                return 53;
            case KeyEvent.VK_NUMPAD0:
                return 55;
            case KeyEvent.VK_DECIMAL:
                return 57;
            case KeyEvent.VK_ENTER:
                if(keyEvent.getKeyLocation()==KeyEvent.KEY_LOCATION_NUMPAD)
                    return 59;
                break;
            case KeyEvent.VK_NUMPAD1:
                return 60;
            case KeyEvent.VK_NUMPAD2:
                return 62;
            case KeyEvent.VK_NUMPAD3:
                return 64;
            case KeyEvent.VK_NUMPAD4:
                return 65;
            case KeyEvent.VK_NUMPAD5:
                return 67;
            case KeyEvent.VK_NUMPAD6:
                return 69;
            case KeyEvent.VK_NUMPAD7:
                return 71;
            case KeyEvent.VK_NUMPAD8:
                return 72;
            case KeyEvent.VK_NUMPAD9:
                return 74;
            case KeyEvent.VK_ADD:
                return 76;
            case KeyEvent.VK_NUM_LOCK:
                return 77;
            case KeyEvent.VK_DIVIDE:
                return 79;
            case KeyEvent.VK_MULTIPLY:
                return 81;
            case KeyEvent.VK_SUBTRACT:
                return 83;
        }
        return 0;
    }

    public void pressKey(KeyEvent keyEvent) {
        int note = parseNote(keyEvent);

        if(note != 0) {
            try {
                ShortMessage shortMessage = new ShortMessage(144, 0, note, 127);
                MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void releasedKey(KeyEvent keyEvent) {
        int note = parseNote(keyEvent);

        if (note != 0) {
            try {
                ShortMessage shortMessage = new ShortMessage(128, 0, note, 127);
                MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
