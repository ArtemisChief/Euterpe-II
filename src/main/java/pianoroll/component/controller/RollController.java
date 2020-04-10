package pianoroll.component.controller;

import pianoroll.component.PianoRoll;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;

import java.util.ArrayList;
import java.util.List;

public class RollController {
    private final PianoRoll pianoRoll;

    private final int amount;

    private final List<Roll> rollList;

    private final List<Integer> triggeredTrackList;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    public RollController(PianoRoll pianoRoll, List<Integer> triggeredTrackList) {
        this.pianoRoll = pianoRoll;

        amount = 100000;

        rollList = new ArrayList<>();

        this.triggeredTrackList = triggeredTrackList;

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;
    }

    public void updateRolls(float deltaTime) {
        if(pianoRoll.isPlaying()) {
            pianoRoll.addTimeSum(deltaTime);

            for (Roll roll : rollList) {
                if (!roll.isUnused()) {
                    roll.setOffsetY(roll.getOffsetY() - pianoRoll.getLengthPerSecond() * deltaTime);

                    if (roll.getOffsetY() - roll.getScaleY() <= 0.0f) {
                        if (!triggeredTrackList.contains(roll.getTrackID())) {
                            roll.setColorID(roll.getTrackID() + 100);
                            triggeredTrackList.add(roll.getTrackID());
                            pianoRoll.getPianoController().pressKey(roll.getTrackID());
                        }

                        roll.setScaleY(roll.getScaleY() - pianoRoll.getLengthPerSecond() * deltaTime);
                    }

                    if (roll.getOffsetY() <= 0.0f) {
                        triggeredTrackList.remove((Integer) roll.getTrackID());
                        pianoRoll.getPianoController().releaseKey(roll.getTrackID());
                        roll.setUnused(true);
                    }

                }
            }
        }
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
        for (int i = lastUnusedRollBlack; i < amount; ++i) {
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
