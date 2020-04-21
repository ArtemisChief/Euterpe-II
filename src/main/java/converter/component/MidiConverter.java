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

    //MIDI文件的分辨率，代表一个四分音符占多少tick
    private int resolution;

    //用于保存上一个音符事件在MUI中对应的字符串
    private MuiNote muiNote = null;

    //note和time用于构造同时音的字符串
    private StringBuilder note = null;
    private StringBuilder time = null;

    //front和latter用于保存最新一行尚未添加到mui中的音乐句子，front是前半部分的旋律，latter是后半部分的节奏
    private StringBuilder front = null;
    private StringBuilder latter = null;

    //mui用于保存最后生成的mui谱
    private StringBuilder mui = null;

//    private StringBuilder test=new StringBuilder();
//    private StringBuilder test2=new StringBuilder();


    //标志是否进入同时音状态
    private boolean sameTime = false;

    //记录尚未添加到mui中的音乐句子有多少个音符，用于判断是否需要另起一行的以保证美观的参考
    private int noteCount = 0;

    //当前播放到的时点
    private double currentTick=0;

    //最后一个尚未播放的音的持续时间
    private double lastDuration=0;

    //记录目前的音量强度
    private int intensity=-1;

    public String converterToMui(File midiFile) {
        MidiParser parser = MidiParser.GetInstance();
        try {

            //通过MIDI解析器解析MIDI文件
            MidiContent midiContent = parser.parse(midiFile);
            resolution = midiContent.getResolution();

            //将解析后按音轨分组的事件列表重新按通道分组
            List<MidiChannel> midiChannels = sortMidiChannel(midiContent.getMidiTrackList());

            mui = new StringBuilder();

            //对每个通道的事件列表进行遍历，一个通道对应生成一个乐谱段paragraph
            for (MidiChannel midiChannel : midiChannels) {
                //添加乐谱段的开头声明以及默认的C调
                mui.append("paragraph Track" + midiChannel.getTrackNumber()
                        + "Channel" + midiChannel.getChannelNumber() + "\n" + "1=C\n");

                //对各变量进行初始化
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



                //用于判断通道9是否已添加打击乐的乐器声明语句
                boolean channel9Instrument=false;


                //对通道内保存的MIDI事件进行遍历
                for (int i=0;i<midiChannel.getMidiEventList().size();++i) {

                    //获取当前MIDI事件
                    MidiEvent midiEvent=midiChannel.getMidiEventList().get(i);

//                    if(midiEvent.getTriggerTick()==98400)
//                        System.out.println("aaa");

                    //如果保存的尚未添加的音乐句子已经有10各音符了，则添加到mui乐谱中（同时音状态时由于之后可能还有同时音，就先不添加）
                    if (noteCount >= 10 && !sameTime) {
                        mui.append(front).append("  <").append(latter).append(">\n");
                        front.delete(0, front.length());
                        latter.delete(0, latter.length());
                        noteCount = 0;
                    }



                    //如果通道9都开始读取到音符MIDI事件了，都还没有进行乐器语句的添加，则手动额外添加乐器语句
                    if(midiChannel.getChannelNumber() == 9&&!channel9Instrument&&(midiEvent instanceof NoteEvent)){
                        mui.append("instrument= -1\n");
                        channel9Instrument=true;
                    }


                    //当读取到的事件是速度事件时，进行速度语句的添加
                    if (midiEvent instanceof BpmEvent) {
                        BpmEvent bpmEvent = (BpmEvent) midiEvent;

                        //changeStatusAddNote是需要进行乐谱属性语句添加时，将目前没添加的音乐句子进行添加的函数
                        changeStatusAddNote(bpmEvent.getTriggerTick());
                        mui.append("speed=" + String.format("%.1f", bpmEvent.getBpm()) + "\n");
                    }

                    //当读取到的事件是乐器事件时，进行乐器语句的添加
                    else if (midiEvent instanceof InstrumentEvent) {
                        InstrumentEvent instrumentEvent = (InstrumentEvent) midiEvent;
                        changeStatusAddNote(instrumentEvent.getTriggerTick());
                        if (midiChannel.getChannelNumber() == 9) {
                            mui.append("instrument= -1\n");
                            channel9Instrument=true;
                        }
                        else
                            mui.append("instrument=" + instrumentEvent.getInstrumentNumber() + "\n");
                    }

                    //当读取到的事件是其他情况，也就是音符事件时，进行音乐句子的添加
                    else {

                        NoteEvent noteEvent = (NoteEvent) midiEvent;

                        //如果读取到的音符事件的音量和目前音量不一致，则进行音量强度语句的添加（mui不支持同时音但音量不同，所以两个音相隔不到32分音符时不进行音量语句的添加）
                        if(!(noteEvent.getTriggerTick() - currentTick<0.125*resolution && lastDuration != 0)&&(noteEvent.getIntensity()!=intensity)) {
                            intensity = noteEvent.getIntensity();
                            changeStatusAddNote(noteEvent.getTriggerTick());
                            mui.append("volume=" + intensity + "\n");
                        }

                        //如果当前的音符事件持续时间不到1tick，则跳过当前事件，应对midi文件里一些奇奇怪怪的事件
                        if(noteEvent.getDurationTicks()<=1)
                            continue;

                        //如果当前音符事件的开始时间和目前播放到的时间相同，且还存在未开始播放的音，则说明当前音符事件和未播放的音是同时音，进入同时音的添加
                        if (noteEvent.getTriggerTick() == currentTick && lastDuration != 0) {
                            sameTime = true;

                            //轩哥说除非重写语义分析不然同时音内不支持用连音拼接时值，所以同时音里的音符只能做规范化，杜绝用连音拼接时值的情况

                            //对上一个音符进行规范化
                            muiNote=muiNote.getStandardMuiNote(resolution);
                            //对当前音符事件进行规范化
                            MuiNote tempMuiNote = getMuiNote(noteEvent.getPitch(), noteEvent.getDurationTicks()).getStandardMuiNote(resolution);

                            //比较哪个音符事件的持续时间更短，更短的放到前面当主音
                            if (tempMuiNote.getDurationTicks() >= muiNote.getDurationTicks()) {
                                note.append(tempMuiNote.getPitchString());
                                time.append(tempMuiNote.getTimeString());
                                noteCount += tempMuiNote.getNoteNumbers();
                                lastDuration=muiNote.getDurationTicks();

                            } else {
                                note.append(muiNote.getPitchString());
                                time.append(muiNote.getTimeString());
                                noteCount += muiNote.getNoteNumbers();
                                muiNote = tempMuiNote;
                                lastDuration = muiNote.getDurationTicks();
                            }

//                            test.append("Add note triggerTick:"+currentTick+"\n");

                        }
                        //当前音符事件的开始时间和目前播放到的时间不同的情况
                        else {

                            //上一个未播放的音符播放完刚好轮到目前音符
                            if (currentTick + lastDuration == noteEvent.getTriggerTick()) {
                                //将上一个音符添加到音乐句子中，然后将目前的音符保存到muiNote中，当前开始播放时间置为目前的音符事件的开始时间
                                addNote();
                                currentTick = noteEvent.getTriggerTick();
                                muiNote = getMaxMuiNote(noteEvent.getPitch(), noteEvent.getDurationTicks()); //11
                                lastDuration=muiNote.getDurationTicks();
                            }

                            //上一个未播放的音符播放完后还有一段空隔才到目前的音符事件的情况
                            else if (currentTick + lastDuration < noteEvent.getTriggerTick()) {

                                //先将上一个音符添加到音乐句子中
                                addNote();

                                //如果时间间隔超过了一个32分音符，则先对应添加休止符，添加完后重新走这次for循环，按新的时间关系来判断该如何添加
                                if(noteEvent.getTriggerTick() - currentTick- lastDuration>=0.125*resolution){
                                    addNote();
                                    MuiNote restNote = getMaxMuiNote(-1, noteEvent.getTriggerTick() - currentTick - lastDuration);
                                    front.append(restNote.getPitchString());
                                    latter.append(restNote.getTimeString());
                                    noteCount += restNote.getNoteNumbers();
                                    currentTick +=restNote.getDurationTicks()+lastDuration;
                                    lastDuration=0;
                                    --i;
                                    continue;
                                }

                                //如果时间间隔都不到一个32分音符，则忽视掉这段时间间隔，把当前音符事件的开始时间修改成前一个音符刚好播放完时，然后重新走这次for循环，按新的时间关系来判断该如何添加
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

                            }

                            //上一个未播放的音符还没播放完时，目前的音符事件就需要开始播放的
                            else {

                                //如果上一个音符事件播了半个32分音符都不到就开始现在的音符事件，则忽视掉这段间隔，把目前的音符事件的开始时间当成和前一个一样，也就是同时音
                                //然后重新走这次for循环，按新的时间关系来判断该如何添加
                                if(noteEvent.getTriggerTick()-currentTick<0.0625*resolution){
                                    NoteEvent newNoteEvent=new NoteEvent(noteEvent.getChannel(),(long)(currentTick),
                                            noteEvent.getPitch(),noteEvent.getIntensity());
                                    newNoteEvent.setDurationTicks(noteEvent.getDurationTicks());
                                    midiChannel.getMidiEventList().add(i,newNoteEvent);
                                    midiChannel.getMidiEventList().remove(i+1);
                                    --i;
                                    continue;
                                }

                                //如果上一个音符事件播了超过半个32分音符但是不到一个32音符就开始现在的音符事件，则把目前的音符事件近似为前一个音符播放了一个32分音符后开始播放
                                //然后重新走这次for循环，按新的时间关系来判断该如何添加
                                if(noteEvent.getTriggerTick()-currentTick<0.125*resolution){
                                    NoteEvent newNoteEvent=new NoteEvent(noteEvent.getChannel(),(long)(currentTick+0.125*resolution),
                                            noteEvent.getPitch(),noteEvent.getIntensity());
                                    newNoteEvent.setDurationTicks(noteEvent.getDurationTicks());
                                    midiChannel.getMidiEventList().add(i,newNoteEvent);
                                    midiChannel.getMidiEventList().remove(i+1);
                                    --i;
                                    continue;
                                }


                                //其他情况，也就是上一个音符事件播了超过一个32分音符甚至更多然后开始播放现在的音符事件的
                                //把上一个音符事件和一个32分休止符组合成同时音，主音为32分音符的休止符，并添加到音乐句子中
                                //然后重新走这次for循环，按新的时间关系来判断该如何添加
                                MuiNote restNote = getMuiNote(-1,0.125*resolution);
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
                                currentTick+=restNote.getDurationTicks();
                                lastDuration=0;
                                --i;
                                continue;

                            }
                        }
                    }

                    --end;

                    //end判断事件列表是否读完，读完时将最后的音符事件按和之前一样的逻辑进行添加
                    //之所以不用for循环里的i来判断，是因为我一开始写的foreach循环，没得i，那时候就用了end
                    //后来发现不用i不行，改成现在的循环方式了，但是end的部分逻辑没错，懒得改了
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

                //当前乐谱段构造完了，加一个end结束符
                mui.append("end\n\n\n");
                intensity=-1;
            }

            //所有的乐谱段构造完后，进行play语句的构造，因为全部都是同时开始，所以无脑&连接就行了
            mui.append("\nplay(");
            for (int i = 0; i < midiChannels.size(); ++i) {
                if (i != 0)
                    mui.append("&");
                mui.append("Track").append(midiChannels.get(i).getTrackNumber()).append("Channel").append(midiChannels.get(i).getChannelNumber());
            }
            mui.append(")");


            //转成最后需要输出的字符串类型
            String result = mui.toString();

            //对冗余的括号进行删减，最多升降5个八度所以无脑循环5次
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

    //获取MuiNote的函数，按音符事件的音高和持续事件对应得到在mui乐谱中旋律的字符串和节奏的字符串
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


    //获取MuiNote的函数，按音符事件的音高和持续事件对应得到在mui乐谱中旋律的字符串和节奏的字符串
    //针对非同时音的版本，即可以用连音来拼接时值
    private MuiNote getMaxMuiNote(int pitch, double durationTicks) {
        int noteNumbers = 0;
        double remainTick = durationTicks + 1;  //部分midi文件会出现durationTicks少1的情况，这里加上
        double newDurationTicks = 0;
        StringBuilder timeString = new StringBuilder();
        do{
            if (remainTick >= resolution * 6) {
                ++noteNumbers;
                timeString.insert(0, "1*");
                newDurationTicks += resolution * 6;
                remainTick -= resolution * 6;
            } else if (remainTick >= resolution * 4) {
                ++noteNumbers;
                timeString.insert(0, "1");
                newDurationTicks += resolution * 4;
                remainTick -= resolution * 4;
            } else if (remainTick >= resolution * 3) {
                ++noteNumbers;
                timeString.insert(0, "2*");
                newDurationTicks += resolution * 3;
                remainTick -= resolution * 3;
            } else if (remainTick >= resolution * 2) {
                ++noteNumbers;
                timeString.insert(0, "2");
                newDurationTicks += resolution * 2;
                remainTick -= resolution * 2;
            } else if (remainTick >= resolution * 1.5) {
                ++noteNumbers;
                timeString.insert(0, "4*");
                newDurationTicks += resolution * 1.5;
                remainTick -= resolution * 1.5;
            } else if (remainTick >= resolution) {
                ++noteNumbers;
                timeString.insert(0, "4");
                newDurationTicks += resolution;
                remainTick -= resolution;
            } else if (remainTick >= resolution * 0.75) {
                ++noteNumbers;
                timeString.insert(0, "8*");
                newDurationTicks += resolution * 0.75;
                remainTick -= resolution * 0.75;
            } else if (remainTick >= resolution * 0.5) {
                ++noteNumbers;
                timeString.insert(0, "8");
                newDurationTicks += resolution * 0.5;
                remainTick -= resolution * 0.5;
            } else if (remainTick >= resolution * 0.375) {
                ++noteNumbers;
                timeString.insert(0, "g*");
                newDurationTicks += resolution * 0.375;
                remainTick -= resolution * 0.375;
            } else if (remainTick >= resolution * 0.25) {
                ++noteNumbers;
                timeString.insert(0, "g");
                newDurationTicks += resolution * 0.25;
                remainTick -= resolution * 0.25;
            } else if (remainTick >= resolution * 0.125) {
                ++noteNumbers;
                timeString.insert(0, "w");
                newDurationTicks += resolution * 0.125;
                remainTick -= resolution * 0.125;
            }else{
                remainTick=0;
            }
        }while(remainTick!=0&&remainTick!=1);
        MuiNote temp=new MuiNote(pitch, timeString.toString(),noteNumbers, newDurationTicks);
        return temp;
    }



    //对从MIDI解析器获得的按音轨分组保存的事件进行按通道再分组的函数
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


    //需要进行乐谱属性语句添加时，将未添加的音符或音乐句子添加到mui乐谱中的函数
    //内部逻辑和进行音符添加时差不多，主要就是怎么补对应的休止符让最后一个音符播完后的时间是目前乐谱属性语句对应的事件的开始时间
    private void changeStatusAddNote(double triggerTick) {
        if(triggerTick>currentTick+lastDuration){
            addNote();
            if(triggerTick - currentTick- lastDuration>=0.0625*resolution){
                if(triggerTick - currentTick- lastDuration>=0.125*resolution) {
                    MuiNote restNote = getMaxMuiNote(-1, triggerTick - currentTick - lastDuration);
                    muiNote = restNote;
                    addNote();
                    currentTick += restNote.getDurationTicks() + lastDuration;
                    lastDuration = 0;
                }
                if(triggerTick - currentTick- lastDuration>=0.0625*resolution) {
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

    //将目前保存着的尚未添加到音乐句子里的音符添加到音乐句子中去
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
