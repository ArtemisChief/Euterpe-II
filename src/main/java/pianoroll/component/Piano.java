package pianoroll.component;

import midi.component.MidiPlayer;
import pianoroll.entity.Key;
import pianoroll.entity.KeyBlack;
import pianoroll.entity.KeyWhite;

import javax.sound.midi.ShortMessage;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Piano {

    private static Piano instance = new Piano();

    public static Piano GetInstance() {
        return instance;
    }

    private static int pitchOffset;

    private static List<Key> keyList;

    private Piano() {

        pitchOffset = 0;

        keyList = new ArrayList<>();

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

    private int parseKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                return 27 + pitchOffset;
            case KeyEvent.VK_DOWN:
                return 29 + pitchOffset;
            case KeyEvent.VK_RIGHT:
                return 31 + pitchOffset;
            case KeyEvent.VK_UP:
                return 32 + pitchOffset;
            case KeyEvent.VK_NUMPAD0:
                return 34 + pitchOffset;
            case KeyEvent.VK_DECIMAL:
                return 36 + pitchOffset;
            case KeyEvent.VK_ENTER:
                if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    return 38 + pitchOffset;
                break;
            case KeyEvent.VK_NUMPAD1:
                return 39 + pitchOffset;
            case KeyEvent.VK_NUMPAD2:
                return 41 + pitchOffset;
            case KeyEvent.VK_NUMPAD3:
                return 43 + pitchOffset;
            case KeyEvent.VK_NUMPAD4:
                return 44 + pitchOffset;
            case KeyEvent.VK_NUMPAD5:
                return 46 + pitchOffset;
            case KeyEvent.VK_NUMPAD6:
                return 48 + pitchOffset;
            case KeyEvent.VK_NUMPAD7:
                return 50 + pitchOffset;
            case KeyEvent.VK_NUMPAD8:
                return 51 + pitchOffset;
            case KeyEvent.VK_NUMPAD9:
                return 53 + pitchOffset;
            case KeyEvent.VK_ADD:
                return 55 + pitchOffset;
            case KeyEvent.VK_NUM_LOCK:
                return 56 + pitchOffset;
            case KeyEvent.VK_DIVIDE:
                return 58 + pitchOffset;
            case KeyEvent.VK_MULTIPLY:
                return 60 + pitchOffset;
            case KeyEvent.VK_SUBTRACT:
                return 62 + pitchOffset;
        }
        return -1;
    }

    public void press(KeyEvent keyEvent) {
        int keyID = parseKey(keyEvent);

        if (keyID != -1) {
            Key key = keyList.get(keyID);
            key.press();
        }
    }

    public void release(KeyEvent keyEvent) {
        int keyID = parseKey(keyEvent);

        if (keyID != -1) {
            Key key = keyList.get(keyID);
            key.release();
        }
    }

    public static void addHalfPitch(){
        pitchOffset++;
    }

    public static void reduceHalfPitch(){
        pitchOffset--;
    }

    public static List<Key> GetKeyList() {
        return keyList;
    }

}
