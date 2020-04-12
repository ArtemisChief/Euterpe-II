package midipaser.component;

import midipaser.entity.*;
import midipaser.entity.events.BpmEvent;
import midipaser.entity.events.InstrumentEvent;
import midipaser.entity.MidiEvent;
import midipaser.entity.events.NoteEvent;
import midipaser.util.MidiParserUtil;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;

public class MidiParser {

    private static final MidiParser instance = new MidiParser();

    public static MidiParser GetInstance() {
        return instance;
    }

    private MidiParser(){}

    public MidiContent parse(File midiFile) {
        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);

            MidiContent midiContent = new MidiContent(sequence.getResolution(),sequence.getTickLength());
            System.out.println("Midi Info - Ticks per quarter note: " + sequence.getResolution());

            int trackCount = 0;

            for (Track track : sequence.getTracks()) {
                MidiTrack midiTrack = new MidiTrack(trackCount++);
                midiContent.getMidiTrackList().add(midiTrack);

                for (int i = 0; i < track.size(); ++i) {
                    javax.sound.midi.MidiEvent midiEvent = track.get(i);

                    byte[] messageData = midiEvent.getMessage().getMessage();

                    long tick = midiEvent.getTick();
                    int type = MidiParserUtil.byteHighToDec(messageData[0]);
                    int channel = MidiParserUtil.byteLowToDec(messageData[0]);

//                    System.out.println(tick + ": " + MidiUtil.bytesToHex(messageData));

                    switch (type) {
                        case 15: {   // meta-event
                            if (channel == 15)
                                if (messageData[1] == 81) {
                                    float bpm = MidiParserUtil.mptToBpm(MidiParserUtil.bytesToInt(new byte[]{messageData[3], messageData[4], messageData[5]}));
                                    midiTrack.getMidiEventList().add(new BpmEvent(tick, bpm));
                                    break;
                                }
                            break;
                        }

                        case 12:    // instrument
                            midiTrack.getMidiEventList().add(new InstrumentEvent(channel, tick, messageData[1]));
                            break;

                        case 9: {   // possibly note-on
                            int pitch = messageData[1];
                            int intensity = messageData[2];

                            if (intensity != 0) {   // definitely note-on
                                midiTrack.getMidiEventList().add(new NoteEvent(channel, tick, pitch, intensity));
                            } else {                // definitely note-off
                                for (int j = midiTrack.getMidiEventList().size() - 1; j >= 0; --j) {
                                    MidiEvent event = midiTrack.getMidiEventList().get(j);

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

                            for (int j = midiTrack.getMidiEventList().size() - 1; j >= 0; --j) {
                                MidiEvent event = midiTrack.getMidiEventList().get(j);

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

            for (MidiTrack midiTrack : midiContent.getMidiTrackList()) {
                System.out.println("---------------------------------------------------------------------------\nTrack No. " + midiTrack.getTrackNumber());
                for (MidiEvent midiEvent : midiTrack.getMidiEventList()) {
                    if (midiEvent instanceof NoteEvent) {
                        NoteEvent noteEvent = (NoteEvent) midiEvent;
                        if (noteEvent.getDurationTicks() == -1)
                            System.out.println("Error");
                        else
                            System.out.println("At tick " + noteEvent.getTriggerTick() + ": Note " + noteEvent.getPitch() + " triggered in channel " + noteEvent.getChannel() + " with intensity " + noteEvent.getIntensity() + " for " + noteEvent.getDurationTicks() + " ticks");
                    } else if (midiEvent instanceof BpmEvent) {
                        BpmEvent bpmEvent = (BpmEvent) midiEvent;
                        System.out.println("At tick " + bpmEvent.getTriggerTick() + ": Controller set BPM to " + bpmEvent.getBpm());
                    } else {
                        InstrumentEvent instrumentEvent = (InstrumentEvent) midiEvent;
                        System.out.println("At tick " + instrumentEvent.getTriggerTick() + ": Change instrument to " + instrumentEvent.getInstrumentNumber());
                    }
                }
            }

            return midiContent;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}