package midi.component;

import javax.sound.midi.ShortMessage;
import java.awt.event.KeyEvent;

public class MidiPiano {

    private static MidiPiano instance = new MidiPiano();

    public static MidiPiano GetInstance() {
        return instance;
    }

    private int toneOffset;

    private MidiPiano() {

        toneOffset = 0;

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 0);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int parseNote(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                return 48 + toneOffset;
            case KeyEvent.VK_DOWN:
                return 50 + toneOffset;
            case KeyEvent.VK_RIGHT:
                return 52 + toneOffset;
            case KeyEvent.VK_UP:
                return 53 + toneOffset;
            case KeyEvent.VK_NUMPAD0:
                return 55 + toneOffset;
            case KeyEvent.VK_DECIMAL:
                return 57 + toneOffset;
            case KeyEvent.VK_ENTER:
                if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    return 59 + toneOffset;
                break;
            case KeyEvent.VK_NUMPAD1:
                return 60 + toneOffset;
            case KeyEvent.VK_NUMPAD2:
                return 62 + toneOffset;
            case KeyEvent.VK_NUMPAD3:
                return 64 + toneOffset;
            case KeyEvent.VK_NUMPAD4:
                return 65 + toneOffset;
            case KeyEvent.VK_NUMPAD5:
                return 67 + toneOffset;
            case KeyEvent.VK_NUMPAD6:
                return 69 + toneOffset;
            case KeyEvent.VK_NUMPAD7:
                return 71 + toneOffset;
            case KeyEvent.VK_NUMPAD8:
                return 72 + toneOffset;
            case KeyEvent.VK_NUMPAD9:
                return 74 + toneOffset;
            case KeyEvent.VK_ADD:
                return 76 + toneOffset;
            case KeyEvent.VK_NUM_LOCK:
                return 77 + toneOffset;
            case KeyEvent.VK_DIVIDE:
                return 79 + toneOffset;
            case KeyEvent.VK_MULTIPLY:
                return 81 + toneOffset;
            case KeyEvent.VK_SUBTRACT:
                return 83 + toneOffset;
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
