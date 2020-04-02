package midi.entity;

public class NoteEvent extends Event {

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
