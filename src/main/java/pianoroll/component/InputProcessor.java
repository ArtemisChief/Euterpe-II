package pianoroll.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputProcessor implements KeyListener {

    private final PianoManager pianoManager;

    private final RollManager rollManager;

    private final ParticleManager particleManager;

    private final List<Integer> keyDownList;

    public InputProcessor(GraphicEngine graphicEngine) {
        this.pianoManager = graphicEngine.getPianoManager();
        this.rollManager = graphicEngine.getRollManager();
        this.particleManager = graphicEngine.getParticleManager();

        keyDownList = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (!keyDownList.contains(e.getKeyCode())) {
                pianoManager.getKeyList().get(trackID).press();

                rollManager.respawnRoll(trackID);
                particleManager.triggerTrack(trackID);

                keyDownList.add(e.getKeyCode());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (keyDownList.contains(e.getKeyCode())) {
                pianoManager.getKeyList().get(trackID).release();

                rollManager.stopUpdatingScaleY(trackID);
                particleManager.suspendTrack(trackID);

                keyDownList.remove((Integer) e.getKeyCode());
            }
        }
    }

    private int parseKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                return 27 + pianoManager.getPitchOffset();
            case KeyEvent.VK_DOWN:
                return 29 + pianoManager.getPitchOffset();
            case KeyEvent.VK_RIGHT:
                return 31 + pianoManager.getPitchOffset();
            case KeyEvent.VK_UP:
                return 32 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD0:
                return 34 + pianoManager.getPitchOffset();
            case KeyEvent.VK_DECIMAL:
                return 36 + pianoManager.getPitchOffset();
            case KeyEvent.VK_ENTER:
                if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    return 38 + pianoManager.getPitchOffset();
                break;
            case KeyEvent.VK_NUMPAD1:
                return 39 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD2:
                return 41 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD3:
                return 43 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD4:
                return 44 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD5:
                return 46 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD6:
                return 48 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD7:
                return 50 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD8:
                return 51 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUMPAD9:
                return 53 + pianoManager.getPitchOffset();
            case KeyEvent.VK_ADD:
                return 55 + pianoManager.getPitchOffset();
            case KeyEvent.VK_NUM_LOCK:
                return 56 + pianoManager.getPitchOffset();
            case KeyEvent.VK_DIVIDE:
                return 58 + pianoManager.getPitchOffset();
            case KeyEvent.VK_MULTIPLY:
                return 60 + pianoManager.getPitchOffset();
            case KeyEvent.VK_SUBTRACT:
                return 62 + pianoManager.getPitchOffset();
        }
        return -1;
    }

}
