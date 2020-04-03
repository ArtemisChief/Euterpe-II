package midipaser.entity.events;

import midipaser.entity.MidiEvent;

public class BpmEvent extends MidiEvent {

    private final float bpm;

    public BpmEvent(long triggerTick, float bpm) {
        super(-1, triggerTick);
        this.bpm = bpm;
    }

    public float getBpm() {
        return bpm;
    }

}
