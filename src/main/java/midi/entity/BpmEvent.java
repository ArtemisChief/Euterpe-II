package midi.entity;

public class BpmEvent extends Event{

    private final float bpm;

    public BpmEvent(int channel, long triggerTick, float bpm) {
        super(channel,triggerTick);
        this.bpm = bpm;
    }

    public float getBpm() {
        return bpm;
    }

}
