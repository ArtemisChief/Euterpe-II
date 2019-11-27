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

    private static List<Integer> pressingWhiteList;
    private static List<Integer> releasingWhiteList;

    private static List<Integer> pressingBlackList;
    private static List<Integer> releasingBlackList;

    private PianoKeys() {

        pitchOffset = 0;

        keyList = new ArrayList<>();

        pressingWhiteList = new ArrayList<>();
        releasingWhiteList = new ArrayList<>();

        pressingBlackList = new ArrayList<>();
        releasingBlackList = new ArrayList<>();

        for (int i = 0; i < 88; i++)
            if (isWhiteKey(i))
                keyList.add(new KeyWhite(i));
            else
                keyList.add(new KeyBlack(i));

        try {
            //todo sustain
            ShortMessage shortMessage = new ShortMessage(176, 0, 64, 127);
            MidiPlayer.GetInstance().getSynthesizer().getReceiver().send(shortMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWhiteKey(int trackID) {
        switch (trackID % 12) {
            case 1:
            case 4:
            case 6:
            case 9:
            case 11:
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
            int trackID = pitch - 21;
            Key key = keyList.get(trackID);
            key.press();

//            if (isWhiteKey(pitch))
//                pressingWhiteList.add(keyWhiteList.indexOf(key));
//            else
//                pressingBlackList.add(keyBlackList.indexOf(key));
        }
    }

    public void releasedKey(KeyEvent keyEvent) {
        int pitch = parsePitch(keyEvent);

        if (pitch != -1) {
            int trackID = pitch - 21;
            Key key = keyList.get(trackID);
            key.release();
        }
    }

    public static List<Key> GetKeyList() {
        return keyList;
    }

}
