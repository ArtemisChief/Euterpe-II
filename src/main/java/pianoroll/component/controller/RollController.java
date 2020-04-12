package pianoroll.component.controller;

import pianoroll.component.Pianoroll;
import pianoroll.component.PianorollCanvas;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class RollController {

    private final List<Roll> rollList;

    public RollController() {
        rollList = new ArrayList<>();
    }

    public void createRoll(int trackID, float offsetY, float scaleY) {
        Roll roll = new Roll(trackID, offsetY, scaleY);
        PianorollCanvas.GetInstance().getRollRenderer().addToUnbindRollList(roll);
        rollList.add(roll);
    }

    public void updateRolls(float distance) {
        for (Roll roll : rollList) {
            roll.setOffsetY(roll.getOffsetY() - distance);

            if (roll.isValid()) {
                if (roll.getOffsetY() - roll.getScaleY() <= 0.0f) {
                    if (!roll.isTriggered()) {
                        roll.setColorID(roll.getTrackID() + Semantic.Color.HIGH_LIGHT);
                        Pianoroll.GetInstance().trigger(roll.getTrackID());
                        roll.setTriggered(true);
//                    Pianoroll.GetInstance().getPianoController().pressKey(roll.getTrackID());
                    }
                }


                if (roll.getOffsetY() <= 0.0f) {
                    roll.setValid(false);
                    roll.setTriggered(false);
                    Pianoroll.GetInstance().suspend(roll.getTrackID());
//                Pianoroll.GetInstance().getPianoController().releaseKey(roll.getTrackID());
                }
            }
        }
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
