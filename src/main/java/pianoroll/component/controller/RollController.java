package pianoroll.component.controller;

import javafx.util.Pair;
import midipaser.component.MidiParser;
import midipaser.entity.MidiContent;
import midipaser.entity.MidiEvent;
import midipaser.entity.MidiTrack;
import midipaser.entity.events.BpmEvent;
import midipaser.entity.events.NoteEvent;
import pianoroll.entity.GraphicElement;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RollController {

    private final int amount;

    private final List<Roll> rollList;

    private final List<Integer> triggeredTrackList;
    private final Queue<Pair<Long,Float>> bpmQueue;

    private int lastUnusedRollWhite;
    private int lastUnusedRollBlack;

    private boolean isLoadMidiFile;

    private double resolution;
    private float firstBpm;
    private float speed;
    private float timeSum;
    private float nextTime;
    private final float quarterLength;

    private final PianoController pianoController;

    public RollController(List<Integer> triggeredTrackList,PianoController pianoController) {
        amount = 100000;

        rollList = new ArrayList<>();
        bpmQueue = new LinkedList<>();

        this.triggeredTrackList = triggeredTrackList;

        lastUnusedRollWhite = 0;
        lastUnusedRollBlack = amount / 2;

        isLoadMidiFile = false;

        resolution=0;
        firstBpm=0.0f;
        speed = Semantic.Roll.DEFAULT_SPEED;
        timeSum=0.0f;
        nextTime=0.0f;
        quarterLength=10.0f;

        this.pianoController=pianoController;
    }

    public void loadMidiFile(File midiFile) {
        isLoadMidiFile = true;

        MidiContent midiContent = MidiParser.GetInstance().parse(midiFile);
        resolution = midiContent.getResolution();


        for (MidiTrack midiTrack : midiContent.getMidiTrackList()) {
            for (MidiEvent midiEvent : midiTrack.getMidiEventList()) {
                if (midiEvent instanceof NoteEvent) {
                    NoteEvent noteEvent = (NoteEvent) midiEvent;

                    double scaleY = noteEvent.getDurationTicks() / resolution * quarterLength -0.15f;
                    double offsetY = noteEvent.getTriggerTick() / resolution * quarterLength + scaleY;
                    int trackID = noteEvent.getPitch() - 21;

                    Roll roll = rollList.get(firstUnusedRoll(trackID));

                    roll.setTrackID(trackID);
                    roll.setColorID(trackID);
                    roll.setOffsetY((float) offsetY);
                    roll.setScaleY((float) scaleY);
                    roll.setUpdatingScaleY(false);
                    roll.setUnused(false);
                }

                if (midiEvent instanceof BpmEvent) {
                    BpmEvent bpmEvent = (BpmEvent) midiEvent;
                    bpmQueue.add(new Pair<>(bpmEvent.getTriggerTick(), bpmEvent.getBpm()));
                }
            }
        }

        if (bpmQueue.peek().getKey() == 0) {
            float bpm = bpmQueue.poll().getValue();
            speed = bpm / 60.0f * quarterLength;
            firstBpm=bpm;
            if (bpmQueue.peek() != null)
                nextTime = (float) (bpmQueue.peek().getKey() / resolution / bpm * 60.0f);
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
            timeSum += deltaTime;

            if (timeSum > nextTime) {
                if (bpmQueue.peek() != null) {
                    float bpm = bpmQueue.poll().getValue();
                    speed = bpm / 60.0f * quarterLength;
                    if (bpmQueue.peek() != null)
                        nextTime = (float) (bpmQueue.peek().getKey() / resolution / firstBpm * 60.0f);
                }
            }

            for (Roll roll : rollList) {
                if (!roll.isUnused()) {
                    roll.setOffsetY(roll.getOffsetY() - speed * deltaTime);

                    if (roll.getOffsetY() - roll.getScaleY() < 0.0f) {
                        if (!triggeredTrackList.contains(roll.getTrackID())) {
                            roll.setColorID(roll.getTrackID() + 100);
                            triggeredTrackList.add(roll.getTrackID());
                            pianoController.trigger(roll.getTrackID());
                        }

                        roll.setScaleY(roll.getScaleY() - speed * deltaTime);
                    }

                    if (roll.getOffsetY() < 0.0f) {
                        triggeredTrackList.remove((Integer) roll.getTrackID());
                        pianoController.suspend(roll.getTrackID());
                        roll.setUnused(true);
                    }

                }
            }
        } else {
            for (Roll roll : rollList) {
                if (!roll.isUnused()) {
                    roll.setOffsetY(roll.getOffsetY() + speed * deltaTime);

                    if (roll.isUpdatingScaleY())
                        roll.setScaleY(roll.getScaleY() + speed * deltaTime);

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
