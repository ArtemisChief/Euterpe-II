package pianoroll.component.inputprocessor;

import pianoroll.component.PianorollCanvas;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MouseProcessor extends MouseMotionAdapter {

    public MouseProcessor() {
        for (int i = 0; i < 88; i++) {
            System.out.println(parsePosX(i));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        float width = (float) PianorollCanvas.GetGlcanvas().getWidth();
        float height = (float) PianorollCanvas.GetGlcanvas().getHeight();
        float ratio = width / height;

        float posX = (2 * e.getX() / width - 1) / 0.02365f * ratio;
        float posY = (1 - 2 * e.getY() / height) / 0.02365f + 26.9f;

        System.out.println(posX + ", " + posY);
    }

    float parsePosX(int index) {
        float width = 2.2f;
        float gap = 0.13f;

        float offsetX = (-52 / 2 + index / 12 * 7) * (width + gap) + (width - gap) / 2;

        int tone = index % 12;

        switch (tone) {
            case 0:
                break;
            case 1:
                offsetX += width / 2;
                break;
            case 2:
                offsetX += width + gap;
                break;
            case 3:
                offsetX += (width + gap) * 2;
                break;
            case 4:
                offsetX += width / 2 + 2 * (width + gap);
                break;
            case 5:
                offsetX += (width + gap) * 3;
                break;
            case 6:
                offsetX += width / 2 + 3 * (width + gap);
                break;
            case 7:
                offsetX += (width + gap) * 4;
                break;
            case 8:
                offsetX += (width + gap) * 5;
                break;
            case 9:
                offsetX += width / 2 + 5 * (width + gap);
                break;
            case 10:
                offsetX += (width + gap) * 6;
                break;
            case 11:
                offsetX += width / 2 + 6 * (width + gap);
                break;
        }

        return offsetX;
    }

}
