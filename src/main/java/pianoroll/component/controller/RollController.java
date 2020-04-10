package pianoroll.component.controller;

import pianoroll.component.Pianoroll;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;

public class RollController {

    private final List<Roll> rollList;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    public RollController() {
        rollList = new ArrayList<>();

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = Semantic.Pianoroll.ROLL_AMOUNT / 2;
    }

    public void updateRolls(float deltaTime) {
        if (Pianoroll.GetInstance().isPlaying()) {
            Pianoroll.GetInstance().addTimeSum(deltaTime);

            float distance = Pianoroll.GetInstance().getLengthPerSecond() * deltaTime;

            for (Roll roll : rollList) {
                if (!roll.isUnused()) {
                    roll.setOffsetY(roll.getOffsetY() - distance);

                    if (roll.getOffsetY() - roll.getScaleY() <= 0.0f) {
                        if (!Pianoroll.GetInstance().getTriggeredTrackList().contains(roll.getTrackID())) {
                            roll.setColorID(roll.getTrackID() + 100);
                            Pianoroll.GetInstance().getTriggeredTrackList().add(roll.getTrackID());
                        }

                        roll.setScaleY(roll.getScaleY() - distance);
                    }

                    if (roll.getOffsetY() <= 0.0f) {
                        Pianoroll.GetInstance().getTriggeredTrackList().remove((Integer) roll.getTrackID());
                        roll.setUnused(true);
                    }

                }
            }
        }
    }

    public void reset() {
        for (Roll roll : rollList)
            roll.setUnused(true);
    }

    public int firstUnusedRoll(int trackID) {
        int unusedRoll;

        if (GraphicElement.IsWhite(trackID))
            unusedRoll = firstUnusedRollWhite();
        else
            unusedRoll = firstUnusedRollBlack();

        return unusedRoll;
    }

    private int firstUnusedRollWhite() {
        for (int i = lastUnusedRollWhite; i < Semantic.Pianoroll.ROLL_AMOUNT / 2; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollWhite = i;
                return i;
            }
        }

        for (int i = 0; i < lastUnusedRollWhite; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollWhite = i;
                return i;
            }
        }

        lastUnusedRollWhite = 0;
        return 0;
    }

    private int firstUnusedRollBlack() {
        for (int i = lastUnusedRollBlack; i < Semantic.Pianoroll.ROLL_AMOUNT; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollBlack = i;
                return i;
            }
        }

        for (int i = Semantic.Pianoroll.ROLL_AMOUNT / 2; i < lastUnusedRollBlack; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollBlack = i;
                return i;
            }
        }

        lastUnusedRollBlack = Semantic.Pianoroll.ROLL_AMOUNT / 2;
        return Semantic.Pianoroll.ROLL_AMOUNT / 2;
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
