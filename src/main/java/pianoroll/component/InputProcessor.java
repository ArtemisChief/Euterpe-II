package pianoroll.component;

import pianoroll.entity.Roll;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputProcessor implements KeyListener{

    private Piano piano;

    private Roller roller;

    private ParticleGenerator particleGenerator;

    private List<Integer> keyDownList;

    public InputProcessor(Piano piano, Roller roller,ParticleGenerator particleGenerator) {
        this.piano = piano;
        this.roller = roller;
        this.particleGenerator = particleGenerator;

        keyDownList = new ArrayList<>();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (!keyDownList.contains(e.getKeyCode())) {
                piano.getKeyList().get(trackID).press();
                roller.newRoll(trackID);
                particleGenerator.addParticlesToTrack(trackID);

                keyDownList.add(e.getKeyCode());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int trackID = parseKey(e);

        if (trackID != -1) {
            if (keyDownList.contains(e.getKeyCode())) {
                piano.getKeyList().get(trackID).release();
                roller.stopUpdatingScaleY(trackID);
                particleGenerator.stopAddingParticlesToTrack(trackID);

                keyDownList.remove(keyDownList.indexOf(e.getKeyCode()));
            }
        }
    }

    private int parseKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                return 27 + piano.getPitchOffset();
            case KeyEvent.VK_DOWN:
                return 29 + piano.getPitchOffset();
            case KeyEvent.VK_RIGHT:
                return 31 + piano.getPitchOffset();
            case KeyEvent.VK_UP:
                return 32 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD0:
                return 34 + piano.getPitchOffset();
            case KeyEvent.VK_DECIMAL:
                return 36 + piano.getPitchOffset();
            case KeyEvent.VK_ENTER:
                if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
                    return 38 + piano.getPitchOffset();
                break;
            case KeyEvent.VK_NUMPAD1:
                return 39 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD2:
                return 41 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD3:
                return 43 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD4:
                return 44 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD5:
                return 46 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD6:
                return 48 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD7:
                return 50 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD8:
                return 51 + piano.getPitchOffset();
            case KeyEvent.VK_NUMPAD9:
                return 53 + piano.getPitchOffset();
            case KeyEvent.VK_ADD:
                return 55 + piano.getPitchOffset();
            case KeyEvent.VK_NUM_LOCK:
                return 56 + piano.getPitchOffset();
            case KeyEvent.VK_DIVIDE:
                return 58 + piano.getPitchOffset();
            case KeyEvent.VK_MULTIPLY:
                return 60 + piano.getPitchOffset();
            case KeyEvent.VK_SUBTRACT:
                return 62 + piano.getPitchOffset();
        }
        return -1;
    }

}