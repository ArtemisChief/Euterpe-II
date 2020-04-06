package pianoroll.component.controller;

import midipaser.component.MidiParser;
import midipaser.entity.MidiContent;
import midipaser.entity.MidiEvent;
import midipaser.entity.MidiTrack;
import midipaser.entity.events.NoteEvent;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RollController {

    private final int amount;

    private final List<Roll> rollList;

    private final List<Integer> triggeredTrackList;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    private boolean isLoadMidiFile;

    private float speed;

    public RollController(List<Integer> triggeredTrackList) {
        amount = 10000;

        rollList = new ArrayList<>();

        this.triggeredTrackList = triggeredTrackList;

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;

        isLoadMidiFile = false;

        speed=30.0f;
    }

    public void loadMidiFile(File midiFile) {
        isLoadMidiFile = true;

        MidiContent midiContent = MidiParser.GetInstance().parse(midiFile);
        double resolution = midiContent.getResolution();


        for (MidiTrack midiTrack : midiContent.getMidiTrackList()) {
            for (MidiEvent midiEvent : midiTrack.getMidiEventList()) {
                if (midiEvent instanceof NoteEvent) {
                    NoteEvent noteEvent = (NoteEvent) midiEvent;

                    double scaleY = noteEvent.getDurationTicks() / resolution * 8.0f;
                    double offsetY = noteEvent.getTriggerTick() / resolution * 8.0f + scaleY;
                    int trackID = noteEvent.getPitch() - 23;

                    Roll roll = rollList.get(firstUnusedRoll(trackID));

                    roll.setTrackID(trackID);
                    roll.setColorID(trackID);
                    roll.setOffsetY((float) offsetY);
                    roll.setScaleY((float) scaleY);
                    roll.setUpdatingScaleY(false);
                    roll.setUnused(false);
                }
            }
        }
    }

    public void unloadMidiFile() {
        isLoadMidiFile = false;

        for (Roll roll : rollList) {
            roll.setUnused(true);
        }
    }

    public void trigger(Integer trackID) {
        Roll roll = rollList.get(firstUnusedRoll(trackID));

        roll.setTrackID(trackID);
        roll.setColorID(trackID + 100);
        roll.setOffsetY(0.0f);
        roll.setScaleY(1.0f);
        roll.setUpdatingScaleY(true);
        roll.setUnused(false);
    }

    public void suspend(Integer trackID) {
        for (Roll roll : rollList) {
            if (roll.getTrackID() == trackID && roll.isUpdatingScaleY()) {
                roll.setUpdatingScaleY(false);
                roll.setColorID(trackID);
            }
        }
    }

    public void updateRolls(float deltaTime) {
        if (isLoadMidiFile) {
            for (Roll roll : rollList) {
                if (!roll.isUnused()) {
                    roll.setOffsetY(roll.getOffsetY() - 10*deltaTime);

                    if (roll.getOffsetY() - roll.getScaleY() < -20.0f)
                        roll.setUnused(true);
                }
            }
        } else {
            float deltaY = deltaTime * speed;

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
    }

    private int firstUnusedRoll(int trackID) {
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
