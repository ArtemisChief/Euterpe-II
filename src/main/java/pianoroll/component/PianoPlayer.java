package pianoroll.component;

import glm.vec._2.Vec2;
import midi.component.MidiPlayer;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;

import javax.sound.midi.ShortMessage;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PianoPlayer {

    private static PianoPlayer instance = new PianoPlayer();

    public static PianoPlayer GetInstance() {
        return instance;
    }

    private List<Key> keyList;
    private List<Key> keyWhiteList;
    private List<Key> keyBlackList;

    private float[] anchorsWhite;
    private float[] anchorsBlack;

    private int pitchOffset;

    private PianoPlayer() {

        keyList = new ArrayList<>();
        keyWhiteList = new ArrayList<>();
        keyBlackList = new ArrayList<>();

        anchorsWhite = new float[52 * 2];
        anchorsBlack = new float[36 * 2];

        Key key;

        for (int i = 21; i < 109; i++) {
            switch (i % 12) {
                case 0:
                case 2:
                case 4:
                case 5:
                case 7:
                case 9:
                case 11:
                    key = new KeyWhite(i);
                    keyWhiteList.add(key);
                    break;
                case 1:
                case 3:
                case 6:
                case 8:
                case 10:
                    key = new KeyBlack(i);
                    keyBlackList.add(key);
                    break;
                default:
                    key = null;
            }
            keyList.add(key);
        }

        float gap = 0.13f;
        float width = 2.2f;
        int count = 52;
        float bottom = -31.3f;

        for (int i = 0; i < count * 2; i++) {
            anchorsWhite[i] = -(count - i) / 2 * (width + gap) - gap / 2;
            anchorsWhite[++i] = bottom;
        }

        for (int i = 0; i < 35 * 2; i++) {
            anchorsBlack[i] = (1 + i / 10 * 7 - count / 2) * (width + gap) - gap / 2;
            anchorsBlack[++i] = bottom;
            anchorsBlack[++i] = (3 + i / 10 * 7 - count / 2) * (width + gap) - gap / 2;
            anchorsBlack[++i] = bottom;
            anchorsBlack[++i] = (4 + i / 10 * 7 - count / 2) * (width + gap) - gap / 2;
            anchorsBlack[++i] = bottom;
            anchorsBlack[++i] = (6 + i / 10 * 7 - count / 2) * (width + gap) - gap / 2;
            anchorsBlack[++i] = bottom;
            anchorsBlack[++i] = (7 + i / 10 * 7 - count / 2) * (width + gap) - gap / 2;
            anchorsBlack[++i] = bottom;
        }
        anchorsBlack[70] = -26 * (2.2f + 0.13f) + (2.2f + 0.13f) * 1 - 0.13f/2 + 70/10*7*(2.2f+0.13f);
        anchorsBlack[71] = bottom;

        pitchOffset = 0;

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
                return 48 + pitchOffset;
            case KeyEvent.VK_DOWN:
                return 50 + pitchOffset;
            case KeyEvent.VK_RIGHT:
                return 52 + pitchOffset;
            case KeyEvent.VK_UP:
                return 53 + pitchOffset;
            case KeyEvent.VK_NUMPAD0:
                return 55 + pitchOffset;
            case KeyEvent.VK_DECIMAL:
                return 57 + pitchOffset;
            case KeyEvent.VK_ENTER:
                if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    return 59 + pitchOffset;
                break;
            case KeyEvent.VK_NUMPAD1:
                return 60 + pitchOffset;
            case KeyEvent.VK_NUMPAD2:
                return 62 + pitchOffset;
            case KeyEvent.VK_NUMPAD3:
                return 64 + pitchOffset;
            case KeyEvent.VK_NUMPAD4:
                return 65 + pitchOffset;
            case KeyEvent.VK_NUMPAD5:
                return 67 + pitchOffset;
            case KeyEvent.VK_NUMPAD6:
                return 69 + pitchOffset;
            case KeyEvent.VK_NUMPAD7:
                return 71 + pitchOffset;
            case KeyEvent.VK_NUMPAD8:
                return 72 + pitchOffset;
            case KeyEvent.VK_NUMPAD9:
                return 74 + pitchOffset;
            case KeyEvent.VK_ADD:
                return 76 + pitchOffset;
            case KeyEvent.VK_NUM_LOCK:
                return 77 + pitchOffset;
            case KeyEvent.VK_DIVIDE:
                return 79 + pitchOffset;
            case KeyEvent.VK_MULTIPLY:
                return 81 + pitchOffset;
            case KeyEvent.VK_SUBTRACT:
                return 83 + pitchOffset;
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

    public float[] getAnchorsWhite() {
        return anchorsWhite;
    }

    public float[] getAnchorsBlack() {
        return anchorsBlack;
    }

}
