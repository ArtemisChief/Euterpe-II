package midi.entity;

public class InstrumentEvent extends Event{

    private final int instrumentNumber;

    public InstrumentEvent(int channel, long triggerTick, int instrumentNumber) {
        super(channel, triggerTick);
        this.instrumentNumber = instrumentNumber;
    }

    public int getInstrumentNumber() {
        return instrumentNumber;
    }

}
