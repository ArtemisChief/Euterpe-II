package midi.component;

import midi.util.MidiUtil;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;

public class MidiConverter {


    public void convert(File midiFile) {
        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);

            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); ++i) {
                    MidiEvent midiEvent = track.get(i);

                    byte[] messageData = midiEvent.getMessage().getMessage();

                    long tick = midiEvent.getTick();
                    char type = MidiUtil.byteHighToHex(messageData[0]);
                    char channel = MidiUtil.byteLowToHex(messageData[0]);

//                    System.out.println(tick+": "+MidiUtil.bytesToHex(messageData));

                    switch (type) {
                        case 'F':   // meta-event
                            if (channel == 'F')
                                if (messageData[1] == 81) {
                                    float bpm = MidiUtil.mptToBpm(MidiUtil.bytesToInt(new byte[]{messageData[3], messageData[4], messageData[5]}));

                                    System.out.println(tick + ": Set BPM to" + bpm);
                                    break;
                                }
                            break;

                        case '9':   // note-on
                            int note = messageData[1];
                            int intensity = messageData[2];

                            if (intensity != 0)
                                System.out.println(tick + ": On channel " + channel + " open note " + note + " intensity " + intensity);
                            else
                                continue;

                            for (int j = i + 1; j < track.size(); ++j) {
                                MidiEvent uncertainMidiEvent = track.get(j);

                                byte[] uncertainMessageData = uncertainMidiEvent.getMessage().getMessage();

                                char uncertainType = MidiUtil.byteHighToHex(uncertainMessageData[0]);
                                char uncertainChannel = MidiUtil.byteLowToHex(uncertainMessageData[0]);

                                if (uncertainChannel == channel && uncertainMessageData[1] == messageData[1]) {
                                    if ((uncertainType == '9' && uncertainMessageData[2] == 0) || uncertainType == '8') {

                                        long uncertainTick = uncertainMidiEvent.getTick();
                                        System.out.println(uncertainTick + ": On channel " + channel + " close note " + note);
                                        break;
                                    }
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}