package pianoroll.component;

import javafx.util.Pair;
import midipaser.component.MidiParser;
import midipaser.entity.MidiContent;
import midipaser.entity.MidiEvent;
import midipaser.entity.MidiTrack;
import midipaser.entity.events.BpmEvent;
import midipaser.entity.events.NoteEvent;
import pianoroll.component.controller.BackgroundController;
import pianoroll.component.controller.ParticleController;
import pianoroll.component.controller.PianoController;
import pianoroll.component.controller.RollController;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PianoRoll {

    private final List<Integer> triggeredTrackList;

    private final RollController rollController;
    private final PianoController pianoController;
    private final BackgroundController backgroundController;
    private final ParticleController particleController;

    private final GraphicEngine graphicEngine;
    private final InputProcessor inputProcessor;

    private float timeSum;

    private final Queue<Pair<Float, Float>> lengthPerSecondQueue;

    private float lengthPerSecond = Semantic.Pianoroll.DEFAULT_LENGTH_PER_SECOND;

    private boolean isPlaying = false;

    public PianoRoll() {
        triggeredTrackList = new ArrayList<>();

        rollController = new RollController(this, triggeredTrackList);
        pianoController = new PianoController(triggeredTrackList);
        backgroundController = new BackgroundController(this);
        particleController = new ParticleController(triggeredTrackList);

        graphicEngine = new GraphicEngine(this);
        inputProcessor = new InputProcessor(this);

        timeSum = 0.0f;
        lengthPerSecondQueue = new LinkedList<>();
    }

    public void trigger(Integer trackID) {
        triggeredTrackList.add(trackID);
    }

    public void suspend(Integer trackID) {
        triggeredTrackList.remove(trackID);
    }

    public void loadMidiFile(File midiFile) {
        MidiContent midiContent = MidiParser.GetInstance().parse(midiFile);
        BpmEvent firstBpmEvent = null;

        for (MidiTrack midiTrack : midiContent.getMidiTrackList()) {
            for (MidiEvent midiEvent : midiTrack.getMidiEventList()) {
                if (midiEvent instanceof NoteEvent) {
                    NoteEvent noteEvent = (NoteEvent) midiEvent;

                    float scaleY = (float) noteEvent.getDurationTicks() / midiContent.getResolution() * Semantic.Pianoroll.LENGTH_PER_CROTCHET - 0.15f;
                    float offsetY = (float) noteEvent.getTriggerTick() / midiContent.getResolution() * Semantic.Pianoroll.LENGTH_PER_CROTCHET + scaleY + 0.15f;
                    int trackID = noteEvent.getPitch() - 21;

                    Roll roll = rollController.getRollList().get(rollController.firstUnusedRoll(trackID));

                    roll.setTrackID(trackID);
                    roll.setColorID(trackID);
                    roll.setOffsetY(offsetY);
                    roll.setScaleY(scaleY);
                    roll.setUnused(false);
                }

                if (midiEvent instanceof BpmEvent) {
                    BpmEvent bpmEvent = (BpmEvent) midiEvent;
                    Pair<Float, Float> lengthPerSecondPair;

                    if (bpmEvent.getTriggerTick() == 0)
                        firstBpmEvent = bpmEvent;

                    lengthPerSecondPair = new Pair<>(bpmEvent.getTriggerTick() / midiContent.getResolution() / firstBpmEvent.getBpm() * 60.0f, bpmEvent.getBpm());

                    if (!lengthPerSecondQueue.contains(lengthPerSecondPair))
                        lengthPerSecondQueue.add(lengthPerSecondPair);
                }
            }
        }

        if (lengthPerSecondQueue.peek().getKey() == 0)
            setLengthPerSecond(lengthPerSecondQueue.poll().getValue());
        else
            setLengthPerSecond(120);
    }

    public void unloadMidiFile() {
        for (Roll roll : rollController.getRollList()) {
            roll.setUnused(true);
        }

        lengthPerSecondQueue.clear();
        timeSum = 0.0f;
        isPlaying = false;
    }

    public PianoController getPianoController() {
        return pianoController;
    }

    public RollController getRollController() {
        return rollController;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public BackgroundController getBackgroundController() {
        return backgroundController;
    }

    public GraphicEngine getGraphicEngine() {
        return graphicEngine;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public void addTimeSum(float deltaTime) {
        timeSum += deltaTime;

        if (lengthPerSecondQueue.peek() != null) {
            if (timeSum > lengthPerSecondQueue.peek().getKey()) {
                setLengthPerSecond(lengthPerSecondQueue.poll().getValue());
            }
        }
    }

    public void setLengthPerSecond(float bpm) {
        lengthPerSecond = bpm / 60.0f * Semantic.Pianoroll.LENGTH_PER_CROTCHET;
    }

    public float getLengthPerSecond() {
        return lengthPerSecond;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

}
