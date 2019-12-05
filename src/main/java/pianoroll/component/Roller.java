package pianoroll.component;

import pianoroll.entity.*;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class Roller {

    private float speed = 25.0f;

    private List<Roll> rollList;

    public Roller() {
        rollList = new ArrayList<>();
    }

    public void newRoll(int trackID) {
        Roll roll;
        int vbo;

        if (Key.IsWhite(trackID)) {
            roll = new RollWhite(trackID);
            vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.VERTEX_ROLLWHITE);
        } else {
            roll = new RollBlack(trackID);
            vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.VERTEX_ROLLBLACK);
        }

        roll.setVbo(vbo);
        roll.setUpdatingScaleY(true);
        PianorollCanvas.OfferGraphicElementQueue(roll);
        rollList.add(roll);
    }

    public void stopUpdatingScaleY(int trackID) {
        for (Roll roll : rollList) {
            if (roll.getTrackID() == trackID && roll.isUpdatingScaleY()) {
                roll.setUpdatingScaleY(false);
                roll.setColorID(trackID);
            }
        }
    }

    public float getSpeed() {
        return speed;
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
