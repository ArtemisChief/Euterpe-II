package midi.component;

import midi.entity.BpmEvent;
import midi.entity.Event;
import midi.entity.NoteEvent;
import midi.util.MidiUtil;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MidiParser {

    private static final MidiParser instance = new MidiParser();

    public static MidiParser GetInstance() {
        return instance;
    }

    private int resolution;

    private final List<Event> eventList;

    private MidiParser() {
        resolution = 0;
        eventList = new ArrayList<>();
    }

    public void parse(File midiFile) {
        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);
            resolution = sequence.getResolution();
            System.out.println("Ticks per quarter note: " + sequence.getResolution());

            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); ++i) {
                    MidiEvent midiEvent = track.get(i);

                    byte[] messageData = midiEvent.getMessage().getMessage();

                    long tick = midiEvent.getTick();
                    int type = MidiUtil.byteHighToDec(messageData[0]);
                    int channel = MidiUtil.byteLowToDec(messageData[0]);

                    switch (type) {
                        case 15: {   // meta-event
                            if (channel == 15)
                                if (messageData[1] == 81) {
                                    float bpm = MidiUtil.mptToBpm(MidiUtil.bytesToInt(new byte[]{messageData[3], messageData[4], messageData[5]}));
                                    eventList.add(new BpmEvent(channel, tick, bpm));
                                    break;
                                }
                            break;
                        }

                        case 9: {   // possibly note-on
                            int pitch = messageData[1];
                            int intensity = messageData[2];

                            if (intensity != 0) {   // definitely note-on
                                eventList.add(new NoteEvent(channel, tick, pitch, intensity));
                            } else {                // definitely note-off
                                for (int j = eventList.size() - 1; j >= 0; --j) {
                                    Event event = eventList.get(j);

                                    if (event instanceof NoteEvent) {
                                        NoteEvent noteEvent = (NoteEvent) event;

                                        if (noteEvent.getChannel() == channel && noteEvent.getPitch() == pitch && noteEvent.getDurationTicks() == -1) {
                                            noteEvent.setDurationTicks(tick - noteEvent.getTriggerTick());
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }

                        case 8: {   // definitely note-off
                            int pitch = messageData[1];

                            for (int j = eventList.size() - 1; j >= 0; --j) {
                                Event event = eventList.get(j);

                                if (event instanceof NoteEvent) {
                                    NoteEvent noteEvent = (NoteEvent) event;

                                    if (noteEvent.getChannel() == channel && noteEvent.getPitch() == pitch && noteEvent.getDurationTicks() == -1) {
                                        noteEvent.setDurationTicks(tick - noteEvent.getTriggerTick());
                                        break;
                                    }
                                }
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            }

            for (Event event : eventList) {
                if (event instanceof NoteEvent) {
                    NoteEvent noteEvent = (NoteEvent) event;
                    if (noteEvent.getDurationTicks() == -1)
                        System.out.println("Error");
                    else
                        System.out.println("At tick " + noteEvent.getTriggerTick() + ": Note " + noteEvent.getPitch() + " triggered in channel " + noteEvent.getChannel() + " with intensity " + noteEvent.getIntensity() + " for " + noteEvent.getDurationTicks() + " ticks");
                } else {
                    BpmEvent bpmEvent = (BpmEvent) event;
                    System.out.println("At tick " + bpmEvent.getTriggerTick() + ": Controller set BPM to " + bpmEvent.getBpm());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getResolution() {
        return resolution;
    }

    public List<Event> getEventList() {
        return eventList;
    }

}