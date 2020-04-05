package pianoroll.component.controller;

import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RollController {

    private final int amount;

    private final List<Roll> rollList;

    private final List<Integer> triggeredTrackList;
    private final List<Integer> triggeringTrackList;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    public RollController(List<Integer> triggeredTrackList) {
        amount = 100;

        rollList = new ArrayList<>();

        this.triggeredTrackList = triggeredTrackList;
        triggeringTrackList = new ArrayList<>();

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;
    }

    public void trigger(Integer trackID) {
        if (!triggeredTrackList.contains(trackID))
            triggeredTrackList.add(trackID);
    }

    public void suspend(Integer trackID) {
        for (Roll roll : rollList) {
            if (roll.getTrackID() == trackID && roll.isUpdatingScaleY()) {
                roll.setUpdatingScaleY(false);
                roll.setColorID(trackID);
            }
        }

        if (triggeredTrackList.contains(trackID))
            triggeredTrackList.remove(trackID);

        triggeringTrackList.remove(trackID);
    }

    public void updateRolls(float deltaTime) {
        // spawn a new roll if needed
        for (Integer trackID : triggeredTrackList) {
            if (!triggeringTrackList.contains(trackID)) {
                int unusedRoll;

                if (GraphicElement.IsWhite(trackID))
                    unusedRoll = firstUnusedRollWhite();
                else
                    unusedRoll = firstUnusedRollBlack();

                Roll roll = rollList.get(unusedRoll);

                roll.setTrackID(trackID);
                roll.setColorID(trackID + 100);
                roll.setOffsetY(0.0f);
                roll.setScaleY(1.0f);
                roll.setUpdatingScaleY(true);
                roll.setUnused(false);

                triggeringTrackList.add(trackID);
            }
        }

        // update all rolls
        float deltaY = deltaTime * Semantic.Roll.SPEED;

        for (Roll roll : rollList) {
            if (!roll.isUnused()) {
                roll.setOffsetY(roll.getOffsetY() + deltaY);

                if (roll.isUpdatingScaleY())
                    roll.setScaleY(roll.getScaleY() + deltaY);

                if (roll.getOffsetY() - roll.getScaleY() > 80.0f)
                    roll.setUnused(true);
            }
        }
    }

    private int firstUnusedRollWhite() {
        for (int i = lastUnusedRollWhite; i < amount / 2; ++i) {
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
        for (int i = lastUnusedRollBlack; i < amount / 2; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollBlack = i;
                return i;
            }
        }

        for (int i = amount / 2; i < lastUnusedRollBlack; ++i) {
            if (rollList.get(i).isUnused()) {
                lastUnusedRollBlack = i;
                return i;
            }
        }

        lastUnusedRollBlack = amount / 2;
        return amount / 2;
    }

    public int getAmount() {
        return amount;
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
