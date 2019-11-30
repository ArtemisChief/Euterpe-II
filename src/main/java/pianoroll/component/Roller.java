package pianoroll.component;

import pianoroll.entity.*;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class Roller {

    private float speed = 33.0f;

    private List<Roll> rollList;

    public Roller() {
        rollList = new ArrayList<>();
    }

    public void newRoll(int trackID) {
        Roll roll;
        int vbo;

        if (Key.IsWhite(trackID)) {
            roll = new RollWhite(trackID,5);
            vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.VERTEX_ROLLWHITE);
        } else {
            roll = new RollBlack(trackID,5);
            vbo = PianorollCanvas.GetBufferName().get(Semantic.Buffer.VERTEX_ROLLBLACK);
        }

        roll.setVbo(vbo);
        roll.setUpdatingScaleY(true);
        PianorollCanvas.OfferGraphicElementQueue(roll);
        rollList.add(roll);
    }

    public void stopUpdatingScaleY(int trackID) {
        for (Roll roll : rollList) {
            if (roll.getTrackID() == trackID && roll.isUpdatingScaleY())
                roll.setUpdatingScaleY(false);
        }
    }

    public void updateRolls(float deltaTime) {
        for (Roll roll : rollList) {
            roll.update(deltaTime * speed);
        }
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
