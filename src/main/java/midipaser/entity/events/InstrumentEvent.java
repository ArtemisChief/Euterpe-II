package midipaser.entity.events;

import midipaser.entity.MidiEvent;

public class InstrumentEvent extends MidiEvent {

    private final int instrumentNumber;

    public InstrumentEvent(int channel, long triggerTick, int instrumentNumber) {
        super(channel, triggerTick);
        this.instrumentNumber = instrumentNumber;
    }

    public int getInstrumentNumber() {
        return instrumentNumber;
    }

}
