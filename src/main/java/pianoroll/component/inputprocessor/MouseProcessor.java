package pianoroll.component.inputprocessor;

import pianoroll.component.Pianoroll;
import pianoroll.util.Semantic;

import java.awt.event.*;

public class MouseProcessor extends MouseAdapter implements MouseMotionListener {

    private int currentTrackID;

    private boolean isLeftButtonDown;

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isLeftButtonDown) {
            if (getTrackID(getMousePos(e)) == -1) {
                Pianoroll.GetInstance().suspend(currentTrackID);
                Pianoroll.GetInstance().getPianoController().releaseKey(currentTrackID);
                currentTrackID = -1;
            } else if (getTrackID(getMousePos(e)) != currentTrackID) {
                Pianoroll.GetInstance().suspend(currentTrackID);
                Pianoroll.GetInstance().getPianoController().releaseKey(currentTrackID);

                currentTrackID = getTrackID(getMousePos(e));

                Pianoroll.GetInstance().trigger(currentTrackID);
                Pianoroll.GetInstance().getPianoController().pressKey(currentTrackID);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            currentTrackID = getTrackID(getMousePos(e));
            if (currentTrackID != -1) {
                Pianoroll.GetInstance().trigger(currentTrackID);
                Pianoroll.GetInstance().getPianoController().pressKey(currentTrackID);
                isLeftButtonDown=true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (currentTrackID != -1) {
                Pianoroll.GetInstance().suspend(currentTrackID);
                Pianoroll.GetInstance().getPianoController().releaseKey(currentTrackID);
                isLeftButtonDown=false;
            }
        }
    }

    private float[] getMousePos(MouseEvent e) {
        float posX = (2 * e.getX() / (float) Semantic.Canvas.WIDTH - 1) / Semantic.Canvas.SCALE_FACTOR * Semantic.Canvas.RATIO;
        float posY = (1 - 2 * e.getY() / (float) Semantic.Canvas.HEIGHT) / Semantic.Canvas.SCALE_FACTOR + 26.9f;

        return new float[]{posX, posY};
    }

    private int getTrackID(float[] pos) {
        float posX = pos[0];
        float posY = pos[1];

        if (posY <= 0.0f) {
            for (int index = 0; index < Semantic.Piano.KEY_MAX; index += 12) {
                int tempIndex = index + 1;
                if (posX > getOffsetX(tempIndex) - 0.68f && posX < getOffsetX(tempIndex) + 0.68f && posY > -8.3f)
                    return tempIndex;

                tempIndex = index + 4;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 0.68f && posX < getOffsetX(tempIndex) + 0.68f && posY > -8.3f)
                    return tempIndex;

                tempIndex = index + 6;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 0.68f && posX < getOffsetX(tempIndex) + 0.68f && posY > -8.3f)
                    return tempIndex;

                tempIndex = index + 9;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 0.68f && posX < getOffsetX(tempIndex) + 0.68f && posY > -8.3f)
                    return tempIndex;

                tempIndex = index + 11;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 0.68f && posX < getOffsetX(tempIndex) + 0.68f && posY > -8.3f)
                    return tempIndex;
            }

            for (int index = 0; index < Semantic.Piano.KEY_MAX; index += 12) {
                int tempIndex = index;
                if (posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;

                tempIndex = index + 2;
                if (posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;

                tempIndex = index + 3;
                if (posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;

                tempIndex = index + 5;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;

                tempIndex = index + 7;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;

                tempIndex = index + 8;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;

                tempIndex = index + 10;
                if (tempIndex < Semantic.Piano.KEY_MAX && posX > getOffsetX(tempIndex) - 1.113f && posX < getOffsetX(tempIndex) + 1.113f)
                    return tempIndex;
            }
        }

        return -1;
    }

    private float getOffsetX(int index) {
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
