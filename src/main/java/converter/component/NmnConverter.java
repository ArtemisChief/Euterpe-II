package converter.component;

import converter.entity.MidiChannel;
import converter.entity.MuiNote;
import midipaser.component.MidiParser;
import midipaser.entity.MidiContent;
import midipaser.entity.MidiEvent;
import midipaser.entity.MidiTrack;
import midipaser.entity.events.BpmEvent;
import midipaser.entity.events.InstrumentEvent;
import midipaser.entity.events.NoteEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NmnConverter {


    //MIDI文件的分辨率，代表一个四分音符占多少tick
    private int resolution;

    //用于保存上一个音符事件在MUI中对应的字符串
    private MuiNote muiNote = null;


    //标志是否进入同时音状态
    private boolean sameTime = false;

    //记录尚未添加到mui中的音乐句子有多少个音符，用于判断是否需要另起一行的以保证美观的参考
    private int noteCount = 0;

    //当前播放到的时点
    private double currentTick=0;

    //最后一个尚未播放的音的持续时间
    private double lastDuration=0;

    //只保存了乐器或速度事件的MidiChannel
    private List<MidiChannel> status;

    private List<MuiNote> muiNoteList;




    public List<MuiNote> getMuiNoteList(File midiFile) {
        MidiParser parser = MidiParser.GetInstance();
        try {

            status=new ArrayList<>();
            muiNoteList=new ArrayList<>();

            //通过MIDI解析器解析MIDI文件
            MidiContent midiContent = parser.parse(midiFile);
            resolution = midiContent.getResolution();

            //将解析后按音轨分组的事件列表重新按通道分组
            List<MidiChannel> midiChannels = sortMidiChannel(midiContent.getMidiTrackList());

            //对每个通道的事件列表进行遍历，一个通道对应生成一个乐谱段paragraph
            for (MidiChannel midiChannel : midiChannels) {

                MidiChannel temp=new MidiChannel(midiChannel.getTrackNumber(),midiChannel.getChannelNumber());

                //对各变量进行初始化
                currentTick = 0;
                lastDuration = 0;
                sameTime = false;
                muiNote = null;
                int end = midiChannel.getMidiEventList().size();

                //用于判断通道9是否已添加打击乐的乐器声明语句
                boolean channel9Instrument=false;

                //对通道内保存的MIDI事件进行遍历
                for (int i=0;i<midiChannel.getMidiEventList().size();++i) {

                    //获取当前MIDI事件
                    MidiEvent midiEvent=midiChannel.getMidiEventList().get(i);


                    //如果通道9都开始读取到音符MIDI事件了，都还没有进行乐器语句的添加，则手动额外添加乐器语句
                    if(midiChannel.getChannelNumber() == 9&&!channel9Instrument&&(midiEvent instanceof NoteEvent)){
                        temp.getMidiEventList().add(new InstrumentEvent(midiChannel.getChannelNumber(),midiEvent.getTriggerTick(),-1));
                        channel9Instrument=true;
                    }


                    //当读取到的事件是速度事件时，进行速度语句的添加
                    if (midiEvent instanceof BpmEvent) {
                        BpmEvent bpmEvent = (BpmEvent) midiEvent;
                        temp.getMidiEventList().add(midiEvent);
                        //changeStatusAddNote是需要进行乐谱属性语句添加时，将目前没添加的音乐句子进行添加的函数
                        changeStatusAddNote(bpmEvent.getTriggerTick(),midiChannel.getTrackNumber(),midiChannel.getChannelNumber());
                    }

                    //当读取到的事件是乐器事件时，进行乐器语句的添加
                    else if (midiEvent instanceof InstrumentEvent) {
                        InstrumentEvent instrumentEvent = (InstrumentEvent) midiEvent;
                        temp.getMidiEventList().add(midiEvent);
                        changeStatusAddNote(instrumentEvent.getTriggerTick(),midiChannel.getTrackNumber(),midiChannel.getChannelNumber());
                    }

                    //当读取到的事件是其他情况，也就是音符事件时，进行音乐句子的添加
                    else {

                        NoteEvent noteEvent = (NoteEvent) midiEvent;

                        //如果当前的音符事件持续时间不到1tick，则跳过当前事件，应对midi文件里一些奇奇怪怪的事件
                        if(noteEvent.getDurationTicks()<=1)
                            continue;

                        //如果当前音符事件的开始时间和目前播放到的时间相同，且还存在未开始播放的音，则说明当前音符事件和未播放的音是同时音，进入同时音的添加
                        if (noteEvent.getTriggerTick() == currentTick && lastDuration != 0) {
                            sameTime = true;

                            //对上一个音符进行规范化
                            muiNote=muiNote.getStandardMuiNote(resolution);
                            //对当前音符事件进行规范化
                            MuiNote tempMuiNote = getMuiNote(noteEvent.getPitch(), noteEvent.getDurationTicks(),midiChannel.getTrackNumber(),
                                    midiChannel.getChannelNumber(),noteEvent.getTriggerTick()).getStandardMuiNote(resolution);

                            //比较哪个音符事件的持续时间更短，更短的放到前面当主音
                            if (tempMuiNote.getDurationTicks() >= muiNote.getDurationTicks()) {
                                muiNoteList.add(tempMuiNote);
                                lastDuration=muiNote.getDurationTicks();

                            } else {
                                muiNoteList.add(muiNote);
                                muiNote = tempMuiNote;
                                lastDuration = muiNote.getDurationTicks();
                            }

                        }
                        //当前音符事件的开始时间和目前播放到的时间不同的情况
                        else {

                            //上一个未播放的音符播放完刚好轮到目前音符
                            if (currentTick + lastDuration == noteEvent.getTriggerTick()) {
                                //将上一个音符添加到音乐句子中，然后将目前的音符保存到muiNote中，当前开始播放时间置为目前的音符事件的开始时间
                                addNote();
                                currentTick = noteEvent.getTriggerTick();
                                muiNote = getMaxMuiNote(noteEvent.getPitch(), noteEvent.getDurationTicks(),midiChannel.getTrackNumber(),
                                        midiChannel.getChannelNumber(),noteEvent.getTriggerTick());
                                lastDuration=muiNote.getDurationTicks();
                            }

                            //上一个未播放的音符播放完后还有一段空隔才到目前的音符事件的情况
                            else if (currentTick + lastDuration < noteEvent.getTriggerTick()) {

                                //先将上一个音符添加到音乐句子中
                                addNote();

                                //如果时间间隔超过了一个32分音符，则先对应添加休止符，添加完后重新走这次for循环，按新的时间关系来判断该如何添加
                                if(noteEvent.getTriggerTick() - currentTick- lastDuration>=0.125*resolution){
                                    addNote();
                                    MuiNote restNote = getMaxMuiNote(-1, noteEvent.getTriggerTick() - currentTick - lastDuration,midiChannel.getTrackNumber(),
                                            midiChannel.getChannelNumber(),(long)(currentTick + lastDuration));
                                    muiNoteList.add(restNote);
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
                                MuiNote restNote = getMuiNote(-1,0.125*resolution,midiChannel.getTrackNumber(),
                                        midiChannel.getChannelNumber(),(long)currentTick);
                                muiNote=muiNote.getStandardMuiNote(resolution);

                                muiNoteList.add(restNote);
                                muiNoteList.add(muiNote);

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
                        if(muiNote!=null)
                            muiNoteList.add(muiNote);
                    }
                }

                status.add(temp);

            }


            return muiNoteList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取MuiNote的函数，按音符事件的音高和持续事件对应得到在mui乐谱中旋律的字符串和节奏的字符串
    private MuiNote getMuiNote(int pitch, double durationTicks,int trackNumber,int channelNumber,long triggerTick) {
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
        MuiNote temp=new MuiNote(pitch, timeString.toString(),noteNumbers, durationTicks,triggerTick,channelNumber,resolution,trackNumber);
        return temp;
    }


    //获取MuiNote的函数，按音符事件的音高和持续事件对应得到在mui乐谱中旋律的字符串和节奏的字符串
    //针对非同时音的版本，即可以用连音来拼接时值
    private MuiNote getMaxMuiNote(int pitch, double durationTicks,int trackNumber,int channelNumber,long triggerTick) {
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
            }else if (remainTick >= resolution * 0.0625) {
                ++noteNumbers;
                timeString.insert(0, "w");
                newDurationTicks += resolution * 0.125;
                remainTick =0;
            }else{
                remainTick=0;
            }
        }while(remainTick!=0&&remainTick!=1);
        MuiNote temp=new MuiNote(pitch, timeString.toString(),noteNumbers, newDurationTicks,triggerTick,channelNumber,resolution,trackNumber);
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
    private void changeStatusAddNote(double triggerTick,int trackNumber,int channelNumber) {
        if(triggerTick>currentTick+lastDuration){
            addNote();
            if(triggerTick - currentTick- lastDuration>=0.0625*resolution){
                if(triggerTick - currentTick- lastDuration>=0.125*resolution) {
                    MuiNote restNote = getMaxMuiNote(-1, triggerTick - currentTick - lastDuration,
                            trackNumber,channelNumber,(long)(currentTick+lastDuration));
                    muiNote = restNote;
                    addNote();
                    currentTick += restNote.getDurationTicks() + lastDuration;
                    lastDuration = 0;
                }
                if(triggerTick - currentTick- lastDuration>=0.0625*resolution) {
                    MuiNote restNote = getMuiNote(-1, 0.125 * resolution,
                            trackNumber,channelNumber,(long)(currentTick+lastDuration));
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

            MuiNote restNote = getMuiNote(-1, 0.125*resolution,
                    trackNumber,channelNumber,(long)currentTick);
            if(muiNote!=null) {
                muiNote=muiNote.getStandardMuiNote(resolution);

                muiNoteList.add(restNote);
                muiNoteList.add(muiNote);

                sameTime = false;
                muiNote=null;
            }
            else
                muiNoteList.add(restNote);
            currentTick+=0.125*resolution;
            lastDuration=0;
            if(triggerTick>=currentTick)
                changeStatusAddNote(triggerTick,trackNumber,channelNumber);
        }
        addNote();
    }

    //将目前保存着的尚未添加到音乐句子里的音符添加到音乐句子中去
    private void addNote() {
        if (muiNote != null) {
            muiNoteList.add(muiNote);
        }
        muiNote=null;

    }




}
