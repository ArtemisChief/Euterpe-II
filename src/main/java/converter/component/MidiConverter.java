package converter.component;

import converter.entity.MuiNote;
import midipaser.component.MidiParser;
import converter.entity.MidiChannel;
import midipaser.entity.MidiContent;
import midipaser.entity.MidiEvent;
import midipaser.entity.MidiTrack;
import midipaser.entity.events.BpmEvent;
import midipaser.entity.events.InstrumentEvent;
import midipaser.entity.events.NoteEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MidiConverter {

    private static final MidiConverter instance = new MidiConverter();

    public static MidiConverter GetInstance() {
        return instance;
    }

    private MidiConverter() {
    }

    private int resolution;

    private MuiNote muiNote = null;

    private StringBuilder note = null;
    private StringBuilder time = null;
    private StringBuilder front = null;
    private StringBuilder latter = null;
    private StringBuilder mui = null;

//    private StringBuilder test=new StringBuilder();
//    private StringBuilder test2=new StringBuilder();

    private boolean sameTime = false;

    private int noteCount = 0;

    private double currentTick=0;
    private double lastDuration=0;

    private int intensity=-1;

    public String converterToMui(File midiFile) {
        MidiParser parser = MidiParser.GetInstance();
        try {

            MidiContent midiContent = parser.parse(midiFile);
            resolution = midiContent.getResolution();
            List<MidiChannel> midiChannels = sortMidiChannel(midiContent.getMidiTrackList());
            mui = new StringBuilder();

            for (MidiChannel midiChannel : midiChannels) {
                mui.append("paragraph Track" + midiChannel.getTrackNumber()
                        + "Channel" + midiChannel.getChannelNumber() + "\n" + "1=C\n");
                noteCount = 0;
                currentTick = 0;
                lastDuration = 0;
                sameTime = false;
                note = new StringBuilder();
                time = new StringBuilder();
                front = new StringBuilder();
                latter = new StringBuilder();
                muiNote = null;
                int end = midiChannel.getMidiEventList().size();

                boolean channel9Instrument=false;

                for (int i=0;i<midiChannel.getMidiEventList().size();++i) {

                    MidiEvent midiEvent=midiChannel.getMidiEventList().get(i);

//                    if(midiEvent.getTriggerTick()==98400)
//                        System.out.println("aaa");

                    if (noteCount >= 10 && !sameTime) {
                        mui.append(front).append("  <").append(latter).append(">\n");
                        front.delete(0, front.length());
                        latter.delete(0, latter.length());
                        noteCount = 0;
                    }

                    if(!channel9Instrument&&!(midiEvent instanceof InstrumentEvent)){
                        mui.append("instrument= -1\n");
                        channel9Instrument=true;
                    }


                    if (midiEvent instanceof BpmEvent) {
                        BpmEvent bpmEvent = (BpmEvent) midiEvent;
                        changeStatusAddNote(bpmEvent.getTriggerTick());
                        mui.append("speed=" + String.format("%.1f", bpmEvent.getBpm()) + "\n");
                    } else if (midiEvent instanceof InstrumentEvent) {
                        InstrumentEvent instrumentEvent = (InstrumentEvent) midiEvent;
                        changeStatusAddNote(instrumentEvent.getTriggerTick());
                        if (midiChannel.getChannelNumber() == 9) {
                            mui.append("instrument= -1\n");
                            channel9Instrument=true;
                        }
                        else
                            mui.append("instrument=" + instrumentEvent.getInstrumentNumber() + "\n");
                    } else {
                        NoteEvent noteEvent = (NoteEvent) midiEvent;

                        if(!(noteEvent.getTriggerTick() - currentTick<0.125*resolution && lastDuration != 0)&&(noteEvent.getIntensity()!=intensity)) {
                            intensity = noteEvent.getIntensity();
                            changeStatusAddNote(noteEvent.getTriggerTick());
                            mui.append("volume=" + intensity + "\n");
                        }

                        if(noteEvent.getDurationTicks()<=1)
                            continue;
                        if (noteEvent.getTriggerTick() == currentTick && lastDuration != 0) {
                            sameTime = true;
                            muiNote=muiNote.getStandardMuiNote(resolution);
                            MuiNote tempMuiNote = getMuiNote(noteEvent.getPitch(), noteEvent.getDurationTicks()).getStandardMuiNote(resolution);
                            if (tempMuiNote.getDurationTicks() >= muiNote.getDurationTicks()) {
                                note.append(tempMuiNote.getPitchString());
                                time.append(tempMuiNote.getTimeString());
                                noteCount += tempMuiNote.getNoteNumbers();

                            } else {
                                note.append(muiNote.getPitchString());
                                time.append(muiNote.getTimeString());
                                noteCount += muiNote.getNoteNumbers();
                                muiNote = tempMuiNote;
                                lastDuration = muiNote.getDurationTicks();
                            }

//                            test.append("Add note triggerTick:"+currentTick+"\n");

                        } else {
                            if (currentTick + lastDuration == noteEvent.getTriggerTick()) {
                                addNote();
                                currentTick = noteEvent.getTriggerTick();
                                muiNote = getMuiNote(noteEvent.getPitch(), noteEvent.getDurationTicks()).getStandardMuiNote(resolution); //11
                                lastDuration=muiNote.getDurationTicks();
                            } else if (currentTick + lastDuration < noteEvent.getTriggerTick()) {
                                addNote();
                                if(noteEvent.getTriggerTick() - currentTick- lastDuration>=0.125*resolution){
                                    addNote();
                                    MuiNote restNote = getMuiNote(-1, noteEvent.getTriggerTick() - currentTick - lastDuration);
                                    front.append(restNote.getPitchString());
                                    latter.append(restNote.getTimeString());
                                    noteCount += restNote.getNoteNumbers();
                                    currentTick +=restNote.getDurationTicks()+lastDuration;
                                    lastDuration=0;
                                    --i;
                                    continue;
                                }
                                else {
                                    currentTick+=lastDuration;
                                    lastDuration=0;
                                    NoteEvent newNoteEvent=new NoteEvent(noteEvent.getChannel(),(long)(currentTick),
                                            noteEvent.getPitch(),noteEvent.getIntensity());
                                    newNoteEvent.setDurationTicks(noteEvent.getDurationTicks());
                                    midiChannel.getMidiEventList().add(i,newNoteEvent);
                                    midiChannel.getMidiEventList().remove(i+1);
                                    --i;
                                    continue;
                                }

                            } else {

                                if(noteEvent.getTriggerTick()-currentTick<0.0625*resolution){
                                    NoteEvent newNoteEvent=new NoteEvent(noteEvent.getChannel(),(long)(currentTick),
                                            noteEvent.getPitch(),noteEvent.getIntensity());
                                    newNoteEvent.setDurationTicks(noteEvent.getDurationTicks());
                                    midiChannel.getMidiEventList().add(i,newNoteEvent);
                                    midiChannel.getMidiEventList().remove(i+1);
                                    --i;
                                    continue;
                                }

                                if(noteEvent.getTriggerTick()-currentTick<0.125*resolution){
                                    NoteEvent newNoteEvent=new NoteEvent(noteEvent.getChannel(),(long)(currentTick+0.125*resolution),
                                            noteEvent.getPitch(),noteEvent.getIntensity());
                                    newNoteEvent.setDurationTicks(noteEvent.getDurationTicks());
                                    midiChannel.getMidiEventList().add(i,newNoteEvent);
                                    midiChannel.getMidiEventList().remove(i+1);
                                    --i;
                                    continue;

                                }

                                MuiNote restNote = getMuiNote(-1, 0.125*resolution);
                                if(muiNote!=null) {
                                    restNote=restNote.getStandardMuiNote(resolution);
                                    muiNote=muiNote.getStandardMuiNote(resolution);
                                    note.insert(0, "|" + restNote.getPitchString() + muiNote.getPitchString()).append("|");
                                    time.insert(0, restNote.getTimeString() + muiNote.getTimeString());

//                                    test.append("Add note triggerTick:"+currentTick+"\n");

                                    front.append(note);
                                    note.delete(0, note.length());
                                    latter.append(time);
                                    time.delete(0, time.length());
                                    noteCount += muiNote.getNoteNumbers() + restNote.getNoteNumbers();
                                    sameTime = false;
                                    muiNote=null;
                                }
                                else {
                                    front.append(restNote.getPitchString());
                                    latter.append(restNote.getTimeString());
                                    noteCount += restNote.getNoteNumbers();
                                }
                                currentTick+=0.125*resolution;
                                lastDuration=0;
                                --i;
                                continue;

                            }
                        }
                    }

                    --end;

                    if (end == 0) {
                        if (noteCount >= 10 && !sameTime) {
                            mui.append(front).append("  <").append(latter).append(">\n");
                            front.delete(0, front.length());
                            latter.delete(0, latter.length());
                        }
                        if (sameTime) {
                            note.insert(0, "|" + muiNote.getPitchString()).append("|");
                            time.insert(0, muiNote.getTimeString());

//                            test.append("Add note triggerTick:"+currentTick+"\n");

                            front.append(note);
                            latter.append(time);
                            note.delete(0, note.length());
                            time.delete(0, time.length());
                            sameTime = false;
                        } else if(muiNote!=null){
                            front.append(muiNote.getPitchString());
                            latter.append(muiNote.getTimeString());

//                            test.append("Add note triggerTick:"+currentTick+"\n");

                        }
                        if(front.length()!=0) {
                            mui.append(front).append("  <").append(latter).append(">\n");
                            front.delete(0, front.length());
                            latter.delete(0, latter.length());
                        }
                    }
                }
                mui.append("end\n\n\n");
                intensity=-1;
            }
            mui.append("\nplay(");
            for (int i = 0; i < midiChannels.size(); ++i) {
                if (i != 0)
                    mui.append("&");
                mui.append("Track").append(midiChannels.get(i).getTrackNumber()).append("Channel").append(midiChannels.get(i).getChannelNumber());
            }
            mui.append(")");


            String result = mui.toString();

            for (int i = 0; i < 5; i++)
                result = result.replaceAll("\\)\\(", "").replaceAll("\\]\\[", "");


//            for (MidiChannel midiChannel:midiChannels) {
//                for (int i=0;i<midiChannel.getMidiEventList().size();++i) {
//                    MidiEvent midiEvent=midiChannel.getMidiEventList().get(i);
//                    if (midiEvent instanceof NoteEvent) {
//                        test2.append("Add note triggerTick:"+midiEvent.getTriggerTick()+"\n");
//                    }
//                }
//            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "转换过程中出现错误，所选取文件为不支持的midi文件";
        }
    }

    private MuiNote getMuiNote(int pitch, double durationTicks) {
        int noteNumbers = 0;
        double remainTick = durationTicks + 1;  //部分midi文件会出现durationTicks少1的情况，这里加上
        double minDurationTicks = 0;
        StringBuilder timeString = new StringBuilder();
        do{
            if (remainTick >= resolution * 6) {
                ++noteNumbers;
                timeString.insert(0, "1*");
                minDurationTicks = resolution * 6;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 4) {
                ++noteNumbers;
                timeString.insert(0, "1");
                minDurationTicks = resolution * 4;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 3) {
                ++noteNumbers;
                timeString.insert(0, "2*");
                minDurationTicks = resolution * 3;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 2) {
                ++noteNumbers;
                timeString.insert(0, "2");
                minDurationTicks = resolution * 2;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 1.5) {
                ++noteNumbers;
                timeString.insert(0, "4*");
                minDurationTicks = resolution * 1.5;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution) {
                ++noteNumbers;
                timeString.insert(0, "4");
                minDurationTicks = resolution;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 0.75) {
                ++noteNumbers;
                timeString.insert(0, "8*");
                minDurationTicks = resolution * 0.75;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 0.5) {
                ++noteNumbers;
                timeString.insert(0, "8");
                minDurationTicks = resolution * 0.5;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 0.375) {
                ++noteNumbers;
                timeString.insert(0, "g*");
                minDurationTicks = resolution * 0.375;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 0.25) {
                ++noteNumbers;
                timeString.insert(0, "g");
                minDurationTicks = resolution * 0.25;
                remainTick -= minDurationTicks;
            } else if (remainTick >= resolution * 0.125) {
                ++noteNumbers;
                timeString.insert(0, "w");
                minDurationTicks = resolution * 0.125;
                remainTick -= minDurationTicks;
            } else if(remainTick >= resolution * 0.125*0.5){
                //时值多于32分音符的一半但少于32分音符的按32分音符处理
                if(pitch==-1) {
                    durationTicks-=remainTick;
                    break;
                }
                ++noteNumbers;
                timeString.insert(0, "w");
                double roundingDuration = resolution * 0.125 - remainTick;
                durationTicks+=roundingDuration;
                remainTick=0;
            }else if(remainTick==durationTicks) {
                ++noteNumbers;
                timeString.insert(0, "w");
                durationTicks=0.125*resolution;
            }else{
                remainTick=0;
            }

        }while(pitch==-1&&remainTick!=0&&remainTick!=1);
        MuiNote temp=new MuiNote(pitch, timeString.toString(),noteNumbers, durationTicks);
        return temp;
    }

    private List<MidiChannel> sortMidiChannel(List<MidiTrack> midiTracks) {
        List<MidiChannel> midiChannels = new ArrayList<>();
        List<BpmEvent> bpmEvents = new ArrayList<>();
        for (MidiTrack midiTrack : midiTracks) {
            for (MidiEvent midiEvent : midiTrack.getMidiEventList()) {

                if (midiEvent instanceof BpmEvent) {
                    BpmEvent tempBpmEvent = (BpmEvent) midiEvent;
                    int i=-1;
                    for (BpmEvent bpmEvent : bpmEvents) {
                        if (tempBpmEvent.getTriggerTick() == bpmEvent.getTriggerTick())
                            i=bpmEvents.indexOf(bpmEvent);
                    }
                    if (i!=-1)
                        bpmEvents.remove(i);
                    bpmEvents.add(tempBpmEvent);
                } else if (midiEvent instanceof InstrumentEvent) {
                    InstrumentEvent tempInstrumentEvent = (InstrumentEvent) midiEvent;
                    MidiChannel tempMidiChannel = null;
                    boolean hasChannel = false;
                    for (MidiChannel midiChannel : midiChannels) {
                        if (midiChannel.getTrackNumber() == midiTrack.getTrackNumber() &&
                                midiChannel.getChannelNumber() == tempInstrumentEvent.getChannel()) {
                            hasChannel = true;
                            tempMidiChannel = midiChannel;
                        }
                    }
                    if (!hasChannel) {
                        tempMidiChannel = new MidiChannel(midiTrack.getTrackNumber(), tempInstrumentEvent.getChannel());
                        midiChannels.add(tempMidiChannel);
                    }
                    tempMidiChannel.getMidiEventList().add(tempInstrumentEvent);
                } else {
                    NoteEvent tempNoteEvent = (NoteEvent) midiEvent;
                    MidiChannel tempMidiChannel = null;
                    boolean hasChannel = false;
                    for (MidiChannel midiChannel : midiChannels) {
                        if (midiChannel.getTrackNumber() == midiTrack.getTrackNumber() &&
                                midiChannel.getChannelNumber() == tempNoteEvent.getChannel()) {
                            hasChannel = true;
                            tempMidiChannel = midiChannel;
                        }
                    }
                    if (!hasChannel) {
                        tempMidiChannel = new MidiChannel(midiTrack.getTrackNumber(), tempNoteEvent.getChannel());
                        midiChannels.add(tempMidiChannel);
                    }
                    tempMidiChannel.getMidiEventList().add(tempNoteEvent);
                }
            }
        }
        for (MidiChannel midiChannel : midiChannels) {
            for (BpmEvent bpmEvent : bpmEvents) {
                for (int i = 0; i < midiChannel.getMidiEventList().size(); ++i) {
                    if (midiChannel.getMidiEventList().get(i).getTriggerTick() >= bpmEvent.getTriggerTick()) {
                        midiChannel.getMidiEventList().add(i, bpmEvent);
                        break;
                    }
                }
            }
        }
        return midiChannels;
    }

    private void changeStatusAddNote(double triggerTick) {
        if(triggerTick>currentTick+lastDuration){
            addNote();
            if(triggerTick - currentTick- lastDuration>=0.0625*resolution){
                while(triggerTick - currentTick- lastDuration>=0.0625*resolution) {
                    MuiNote restNote = getMuiNote(-1, 0.125 * resolution);
                    muiNote = restNote;
                    addNote();
                    currentTick += restNote.getDurationTicks() + lastDuration;
                    lastDuration = 0;
                }
            }else {
                currentTick+=lastDuration;
                lastDuration=0;
            }
        }else if((triggerTick<currentTick+lastDuration)&&(triggerTick-currentTick>=0.0625*resolution)){

                MuiNote restNote = getMuiNote(-1, 0.125*resolution);
                if(muiNote!=null) {
                    muiNote=muiNote.getStandardMuiNote(resolution);
                    note.insert(0, "|" + restNote.getPitchString() + muiNote.getPitchString()).append("|");
                    time.insert(0, restNote.getTimeString() + muiNote.getTimeString());

//                    if(!muiNote.getPitchString().contains("0"))
//                        test.append("Add note triggerTick:"+currentTick+"\n");

                    front.append(note);
                    note.delete(0, note.length());
                    latter.append(time);
                    time.delete(0, time.length());
                    noteCount += muiNote.getNoteNumbers() + restNote.getNoteNumbers();
                    sameTime = false;
                    muiNote=null;
                }
                else {
                    front.append(restNote.getPitchString());
                    latter.append(restNote.getTimeString());
                    noteCount += restNote.getNoteNumbers();
                }
                currentTick+=0.125*resolution;
                lastDuration=0;
                if(triggerTick>=currentTick)
                    changeStatusAddNote(triggerTick);
        }

        addNote();
        if (noteCount != 0) {
            mui.append(front).append("  <").append(latter).append(">\n");
            front.delete(0, front.length());
            latter.delete(0, latter.length());
            noteCount = 0;
            mui.append("\n");
        }


    }

    private void addNote() {
        if (sameTime) {
            note.insert(0, "|" + muiNote.getStandardMuiNote(resolution).getPitchString()).append("|");
            time.insert(0, muiNote.getStandardMuiNote(resolution).getTimeString());

//            if(!muiNote.getPitchString().contains("0"))
//                test.append("Add note triggerTick:"+currentTick+"\n");

            front.append(note);
            note.delete(0, note.length());
            latter.append(time);
            time.delete(0, time.length());
            noteCount += muiNote.getStandardMuiNote(resolution).getNoteNumbers();
            sameTime = false;
        } else if (muiNote != null) {
            front.append(muiNote.getPitchString());
            latter.append(muiNote.getTimeString());
            noteCount += muiNote.getNoteNumbers();

//            if(!muiNote.getPitchString().contains("0"))
//                test.append("Add note triggerTick:"+currentTick+"\n");

        }
        muiNote=null;

        if (noteCount >= 10) {
            mui.append(front).append("  <").append(latter).append(">\n");
            front.delete(0, front.length());
            latter.delete(0, latter.length());
            noteCount = 0;
        }


    }




}
