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
import pianoroll.entity.Background;
import pianoroll.entity.Roll;
import pianoroll.util.Semantic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Pianoroll {

    // 单例
    private static final Pianoroll instance = new Pianoroll();

    // 获取单例
    public static Pianoroll GetInstance() {
        return instance;
    }

    private final List<Integer> triggeredTrackList;

    private final RollController rollController;
    private final PianoController pianoController;
    private final BackgroundController backgroundController;
    private final ParticleController particleController;

    private float timeSum;
    private float distanceSum;

    private int index;
    private final List<Pair<Float, Float>> timeBpmList;

    private float lengthPerSecond = Semantic.Pianoroll.DEFAULT_LENGTH_PER_SECOND;

    private boolean isPlaying = false;

    public Pianoroll() {
        triggeredTrackList = new ArrayList<>();

        rollController = new RollController();
        pianoController = new PianoController();
        backgroundController = new BackgroundController();
        particleController = new ParticleController();

        timeSum = 0.0f;
        distanceSum = 0.0f;

        index = 0;
        timeBpmList = new ArrayList<>();
    }

    public void trigger(Integer trackID) {
        triggeredTrackList.add(trackID);
    }

    public void suspend(Integer trackID) {
        triggeredTrackList.remove(trackID);
    }

    public void loadMidiFile(File midiFile) {
        rollController.getRollList().clear();
        backgroundController.getRowList().clear();

        triggeredTrackList.clear();
        timeBpmList.clear();
        timeSum = 0.0f;
        distanceSum = 0.0f;
        index = 0;
        isPlaying = false;

        MidiContent midiContent = MidiParser.GetInstance().parse(midiFile);

        long lastTick = 0;
        float lastBpm = 0.0f;
        float lastTime = 0.0f;

        // 背景小节线
        int rowCount = (int) (midiContent.getTickLength() / midiContent.getResolution() + 3);

        for (int i = 0; i < rowCount; ++i)
            backgroundController.createRow(i * 4 * Semantic.Pianoroll.LENGTH_PER_CROTCHET);

        for (MidiTrack midiTrack : midiContent.getMidiTrackList()) {
            for (MidiEvent midiEvent : midiTrack.getMidiEventList()) {
                // 音符
                if (midiEvent instanceof NoteEvent) {
                    NoteEvent noteEvent = (NoteEvent) midiEvent;

                    float scaleY = (float) noteEvent.getDurationTicks() / midiContent.getResolution() * Semantic.Pianoroll.LENGTH_PER_CROTCHET - 0.15f;
                    float offsetY = (float) noteEvent.getTriggerTick() / midiContent.getResolution() * Semantic.Pianoroll.LENGTH_PER_CROTCHET + scaleY + 0.15f;
                    int trackID = noteEvent.getPitch() - 21;

                    if (trackID < 0 || trackID > 88)
                        continue;

                    rollController.createRoll(trackID,offsetY,scaleY);
                }

                // 变速
                if (midiEvent instanceof BpmEvent) {
                    BpmEvent bpmEvent = (BpmEvent) midiEvent;
                    Pair<Float, Float> timeBpmPair;

                    float time;

                    if (bpmEvent.getTriggerTick() == 0)
                        time = 0;
                    else
                        time = (bpmEvent.getTriggerTick() - lastTick) / (float) midiContent.getResolution() / lastBpm * 60.0f + lastTime;

                    timeBpmPair = new Pair<>(time, bpmEvent.getBpm());

                    lastTime = time;
                    lastTick = bpmEvent.getTriggerTick();
                    lastBpm = bpmEvent.getBpm();

                    if (!timeBpmList.contains(timeBpmPair))
                        timeBpmList.add(timeBpmPair);
                }
            }
        }

        if (timeBpmList.get(index).getKey() == 0)
            calculateLengthPerSecond(timeBpmList.get(index++).getValue());
        else
            calculateLengthPerSecond(120);
    }

    public void reset() {
        for(Roll roll:rollController.getRollList()) {
            roll.setOffsetY(roll.getOffsetY() + distanceSum);
            roll.setColorID(roll.getTrackID());
            roll.setValid(true);
            roll.setTriggered(false);
        }

        for(Background row:backgroundController.getRowList())
            row.setOffsetY(row.getOffsetY() + distanceSum);

        triggeredTrackList.clear();
        timeBpmList.clear();
        timeSum = 0.0f;
        distanceSum = 0.0f;
        index = 0;
        isPlaying = false;
    }

    public void setCurrentTime(float second,long tick,int resolution) {
        triggeredTrackList.clear();
        this.index = 0;

        timeSum = second;
        float distance = tick / (float) resolution * Semantic.Pianoroll.LENGTH_PER_CROTCHET;

        for (Roll roll : rollController.getRollList()) {
            roll.setOffsetY(roll.getOffsetY() + this.distanceSum);
            roll.setColorID(roll.getTrackID());
            roll.setValid(true);
            roll.setTriggered(false);
        }

        for (Background row : backgroundController.getRowList())
            row.setOffsetY(row.getOffsetY() + this.distanceSum);

        distanceSum = distance;

        rollController.updateRolls(distance);
        backgroundController.updateBackground(distance);
    }

    public void update(float deltaTime) {
        if(isPlaying) {
            timeSum += deltaTime;

            while (index < timeBpmList.size() && timeSum > timeBpmList.get(index).getKey())
                calculateLengthPerSecond(timeBpmList.get(index++).getValue());

            float distance = deltaTime * lengthPerSecond;
            distanceSum += distance;

            rollController.updateRolls(distance);
            backgroundController.updateBackground(distance);
        }

        pianoController.updateKeys();
        particleController.updateParticles(deltaTime);
    }

    public void calculateLengthPerSecond(float bpm) {
        lengthPerSecond = bpm / 60.0f * Semantic.Pianoroll.LENGTH_PER_CROTCHET;
    }

    public List<Integer> getTriggeredTrackList() {
        return triggeredTrackList;
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

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

}
