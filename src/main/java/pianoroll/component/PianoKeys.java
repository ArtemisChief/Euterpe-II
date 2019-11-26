package pianoroll.component;

import midi.component.MidiPlayer;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;

import javax.sound.midi.ShortMessage;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PianoKeys {

    private static PianoKeys instance = new PianoKeys();

    public static PianoKeys GetInstance() {
        return instance;
    }

    private int pitchOffset;

    private static List<Key> keyList;

    private static List<Key> keyWhiteList;
    private static List<Key> keyBlackList;

    private static float[] anchorsWhite;
    private static float[] anchorsBlack;

    private static float[] colorsWhite;
    private static float[] colorsBlack;

    private static List<Integer> pressingWhiteList;
    private static List<Integer> releasingWhiteList;

    private static List<Integer> pressingBlackList;
    private static List<Integer> releasingBlackList;

    private PianoKeys() {

        pitchOffset = 0;

        keyList = new ArrayList<>();

        keyWhiteList = new ArrayList<>();
        keyBlackList = new ArrayList<>();

        anchorsWhite = new float[52 * 2];
        anchorsBlack = new float[36 * 2];

        colorsWhite = new float[52 * 3];
        colorsBlack = new float[36 * 3];

        pressingWhiteList=new ArrayList<>();
        releasingWhiteList=new ArrayList<>();

        pressingBlackList=new ArrayList<>();
        releasingBlackList=new ArrayList<>();

        Key key;

        for (int i = 21; i < 109; i++) {
            if (isWhiteKey(i)) {
                key = new KeyWhite(i);
                keyWhiteList.add(key);
            } else {
                key = new KeyBlack(i);
                keyBlackList.add(key);
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
        anchorsBlack[70] = (50 - count / 2) * (width + gap) - gap / 2;
        anchorsBlack[71] = bottom;

        for (int i = 0; i < 52; i++) {
            for (int j = 0; j < 3; j++) {
                colorsWhite[j + 3 * i] = KeyWhite.GetColorData()[j];
            }
        }

        for (int i = 0; i < 36; i++) {
            for (int j = 0; j < 3; j++) {
                colorsBlack[j + 3 * i] = KeyBlack.GetColorData()[j];
            }
        }

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWhiteKey(int pitch) {
        switch (pitch % 12) {
            case 1:
            case 3:
            case 6:
            case 8:
            case 10:
                return false;
            default:
                return true;
        }
    }


    private int parsePitch(KeyEvent keyEvent) {
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
        return -1;
    }

    public void pressKey(KeyEvent keyEvent) {
        int pitch = parsePitch(keyEvent);

        if (pitch != -1) {
            int keyId = pitch - 21;
            Key key = keyList.get(keyId);
            key.press();

            if (isWhiteKey(pitch))
                pressingWhiteList.add(keyWhiteList.indexOf(key));
            else
                pressingBlackList.add(keyBlackList.indexOf(key));
        }
    }

    public void releasedKey(KeyEvent keyEvent) {
        int pitch = parsePitch(keyEvent);

        if (pitch != -1) {
            int keyId = pitch - 21;
            Key key = keyList.get(keyId);
            key.release();

            if (isWhiteKey(pitch)) {
                pressingWhiteList.remove(pressingWhiteList.indexOf(keyWhiteList.indexOf(key)));
                releasingWhiteList.add(keyWhiteList.indexOf(key));
            } else {
                pressingBlackList.remove(pressingBlackList.indexOf(keyBlackList.indexOf(key)));
                releasingBlackList.add(keyBlackList.indexOf(key));
            }
        }
    }

    public static void RemoveFromReleasingWhiteList(int keyId){
        Key key = keyWhiteList.get(keyId);
        releasingWhiteList.remove(releasingWhiteList.indexOf(keyWhiteList.indexOf(key)));
    }

    public static void RemoveFromReleasingBlackList(int keyId){
        Key key = keyBlackList.get(keyId);
        releasingBlackList.remove(releasingBlackList.indexOf(keyBlackList.indexOf(key)));
    }

    public static float[] GetAnchorsWhite() {
        return anchorsWhite;
    }

    public static float[] GetAnchorsBlack() {
        return anchorsBlack;
    }

    public static float[] GetColorsWhite() {
        return colorsWhite;
    }

    public static float[] GetColorsBlack() {
        return colorsBlack;
    }

    public static List<Integer> GetPressingWhiteList() {
        return pressingWhiteList;
    }

    public static List<Integer> GetReleasingWhiteList() {
        return releasingWhiteList;
    }

    public static List<Integer> GetPressingBlackList() {
        return pressingBlackList;
    }

    public static List<Integer> GetReleasingBlackList() {
        return releasingBlackList;
    }

}
