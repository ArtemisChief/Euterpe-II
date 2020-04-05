package pianoroll.component;

import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;

import java.util.ArrayList;
import java.util.List;

public class RollManager {

    private final int amount;

    private final List<Roll> rollList;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    public RollManager() {
        amount = 100;

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;

        rollList = new ArrayList<>();
    }

    public void respawnRoll(int trackID) {
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

    public void stopUpdatingScaleY(int trackID) {
        for (Roll roll : rollList) {
            if (roll.getTrackID() == trackID && roll.isUpdatingScaleY()) {
                roll.setUpdatingScaleY(false);
                roll.setColorID(trackID);
            }
        }
    }

    public int getAmount() {
        return amount;
    }

    public List<Roll> getRollList() {
        return rollList;
    }

}
