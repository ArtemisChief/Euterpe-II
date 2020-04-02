package midi.entity;

public abstract class Event {

    private final int channel;

    private final long triggerTick;

    public Event(int channel, long triggerTick) {
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
