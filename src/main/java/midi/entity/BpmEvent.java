package midi.entity;

public class BpmEvent extends Event {

    private final float bpm;

    public BpmEvent(long triggerTick, float bpm) {
        super(-1, triggerTick);
        this.bpm = bpm;
    }

    public float getBpm() {
        return bpm;
    }

}
