package pianoroll.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InputProcessor implements KeyListener {

    private final PianoRoll pianoRoll;

    private final List<Integer> keyDownList;

    public InputProcessor(PianoRoll pianoRoll) {
        this.pianoRoll = pianoRoll;

        keyDownList = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        pianoRoll.getRollController().loadMidiFile(new File("Luv Letter.mid"));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (!keyDownList.contains(e.getKeyCode())) {
                pianoRoll.trigger(trackID);
                pianoRoll.getPianoController().trigger(trackID);
                pianoRoll.getRollController().trigger(trackID);

                keyDownList.add(e.getKeyCode());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (keyDownList.contains(e.getKeyCode())) {
                pianoRoll.suspend(trackID);
                pianoRoll.getPianoController().suspend(trackID);
                pianoRoll.getRollController().suspend(trackID);

                keyDownList.remove((Integer) e.getKeyCode());
            }
        }
    }

    private int parseKey(KeyEvent keyEvent) {
        int pitchOffset = pianoRoll.getPianoController().getPitchOffset();

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

}
