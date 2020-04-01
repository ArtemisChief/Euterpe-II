package midi.component;

import midi.entity.Note;
import midi.util.MidiUtil;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MidiParser {

    List<Note> noteList = new ArrayList<>();

    public void convert(File midiFile) {
        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);

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

                                    System.out.println("At tick " + tick + ": Controller set BPM to " + bpm);
                                    break;
                                }
                            break;
                        }

                        case 9: {   // possibly note-on
                            int pitch = messageData[1];
                            int intensity = messageData[2];

                            if (intensity != 0) {   // definitely note-on
                                noteList.add(new Note(channel, pitch, intensity, tick));
                            } else {                // definitely note-off
                                for (int j = noteList.size() - 1; j >= 0; --j) {
                                    Note note = noteList.get(j);
                                    if (note.getChannel() == channel && note.getPitch() == pitch && note.getDurationTicks() == -1) {
                                        note.setDurationTicks(tick - note.getTriggerTick());
                                        break;
                                    }
                                }
                            }
                            break;
                        }

                        case 8: {   // definitely note-off
                            int pitch = messageData[1];

                            for (int j = noteList.size() - 1; j >= 0; --j) {
                                Note note = noteList.get(j);
                                if (note.getChannel() == channel && note.getPitch() == pitch && note.getDurationTicks() == -1) {
                                    note.setDurationTicks(tick - note.getTriggerTick());
                                    break;
                                }
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            }

            for (Note note : noteList) {
                if (note.getDurationTicks() == -1)
                    System.out.println("Error");
                else
                    System.out.println("At tick " + note.getTriggerTick() + ": Note " + note.getPitch() + " triggered in channel " + note.getChannel() + " with intensity " + note.getIntensity() + " for " + note.getDurationTicks() + " ticks");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}