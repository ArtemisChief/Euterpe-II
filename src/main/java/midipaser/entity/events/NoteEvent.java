package midipaser.entity.events;

import midipaser.entity.MidiEvent;

public class NoteEvent extends MidiEvent {

    private final int pitch;

    private final int intensity;

    private long durationTicks;

    public NoteEvent(int channel, long triggerTick, int pitch, int intensity) {
        super(channel, triggerTick);
        this.pitch = pitch;
        this.intensity = intensity;
        this.durationTicks = -1;
    }

    public int getPitch() {
        return pitch;
    }

    public int getIntensity() {
        return intensity;
    }

    public long getDurationTicks() {
        return durationTicks;
    }

    public void setDurationTicks(long durationTicks) {
        this.durationTicks = durationTicks;
    }

}
