package midipaser.entity;

public abstract class MidiEvent {

    private final int channel;

    private final long triggerTick;

    public MidiEvent(int channel, long triggerTick) {
        this.channel = channel;
        this.triggerTick = triggerTick;
    }

    public int getChannel() {
        return channel;
    }

    public long getTriggerTick() {
        return triggerTick;
    }

}
