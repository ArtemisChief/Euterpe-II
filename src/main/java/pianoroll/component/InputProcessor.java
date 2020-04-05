package pianoroll.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputProcessor implements KeyListener{

    private final PianoRenderer pianoRenderer;

    private final RollRenderer rollRenderer;

    private final ParticleManager particleManager;

    private final List<Integer> keyDownList;

    public InputProcessor(GraphicEngine graphicEngine) {
        this.pianoRenderer = graphicEngine.getPianoRenderer();
        this.rollRenderer = graphicEngine.getRollRenderer();
        this.particleManager = graphicEngine.getParticleManager();

        keyDownList = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (!keyDownList.contains(e.getKeyCode())) {
                pianoRenderer.getKeyList().get(trackID).press();

                rollRenderer.newRoll(trackID);
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
                pianoRenderer.getKeyList().get(trackID).release();

                rollRenderer.stopUpdatingScaleY(trackID);
                particleManager.suspendTrack(trackID);

                keyDownList.remove((Integer) e.getKeyCode());
            }
        }
    }

    private int parseKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                return 27 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_DOWN:
                return 29 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_RIGHT:
                return 31 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_UP:
                return 32 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD0:
                return 34 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_DECIMAL:
                return 36 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_ENTER:
                if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    return 38 + pianoRenderer.getPitchOffset();
                break;
            case KeyEvent.VK_NUMPAD1:
                return 39 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD2:
                return 41 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD3:
                return 43 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD4:
                return 44 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD5:
                return 46 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD6:
                return 48 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD7:
                return 50 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD8:
                return 51 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUMPAD9:
                return 53 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_ADD:
                return 55 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_NUM_LOCK:
                return 56 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_DIVIDE:
                return 58 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_MULTIPLY:
                return 60 + pianoRenderer.getPitchOffset();
            case KeyEvent.VK_SUBTRACT:
                return 62 + pianoRenderer.getPitchOffset();
        }
        return -1;
    }

}
