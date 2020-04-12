package pianoroll.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InputProcessor implements KeyListener {

    private final List<Integer> keyboardKeyDownList;
    private final List<Integer> pianoKeyDownList;

    public InputProcessor() {
        keyboardKeyDownList = new ArrayList<>();
        pianoKeyDownList=new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (!keyboardKeyDownList.contains(e.getKeyCode())) {
                Pianoroll.GetInstance().trigger(trackID);
                Pianoroll.GetInstance().getPianoController().pressKey(trackID);

                pianoKeyDownList.add(trackID);
                keyboardKeyDownList.add(e.getKeyCode());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (keyboardKeyDownList.contains(e.getKeyCode())) {
                Pianoroll.GetInstance().suspend(trackID);
                Pianoroll.GetInstance().getPianoController().releaseKey(trackID);

                if (pianoKeyDownList.contains(trackID))
                    pianoKeyDownList.remove((Integer) trackID);
                else {
                    Iterator<Integer> iterator = pianoKeyDownList.iterator();
                    while (iterator.hasNext()) {
                        boolean isInList = false;
                        int tid = iterator.next();
                        for (int keyCode : keyboardKeyDownList) {
                            if (parseKey(keyCode) == tid) {
                                isInList = true;
                                break;
                            }
                        }
                        if (!isInList) {
                            iterator.remove();
                            Pianoroll.GetInstance().suspend(tid);
                            Pianoroll.GetInstance().getPianoController().releaseKey(tid);
                        }
                    }
                }
                keyboardKeyDownList.remove((Integer) e.getKeyCode());
            }
        }
    }

    private int parseKey(KeyEvent keyEvent) {
        int pitchOffset = Pianoroll.GetInstance().getPianoController().getPitchOffset();

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

    private int parseKey(int keyCode) {
        int pitchOffset = Pianoroll.GetInstance().getPianoController().getPitchOffset();

        switch (keyCode) {
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
                return 38 + pitchOffset;
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

}
