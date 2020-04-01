package midi.entity;

public class Note {

    private final int channel;

    private final int pitch;

    private final int intensity;

    private final long triggerTick;

    private long durationTicks;

    public Note(int channel, int pitch, int intensity, long triggerTick) {
        this.channel = channel;
        this.pitch = pitch;
        this.intensity = intensity;
        this.triggerTick = triggerTick;
        this.durationTicks = -1;
    }

    public int getChannel() {
        return channel;
    }

    public int getPitch() {
        return pitch;
    }

    public int getIntensity() {
        return intensity;
    }

    public long getTriggerTick() {
        return triggerTick;
    }

    public long getDurationTicks() {
        return durationTicks;
    }

    public void setDurationTicks(long durationTicks) {
        this.durationTicks = durationTicks;
    }
}
