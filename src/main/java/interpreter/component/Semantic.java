package interpreter.component;

import interpreter.entity.Node;
import interpreter.entity.Note;
import interpreter.entity.Paragraph;
import interpreter.entity.Symbol;
import midibuilder.component.MidiFileBuilder;
import midibuilder.component.MidiTrackBuilder;
import midibuilder.entity.MidiFile;
import midibuilder.entity.MidiTrack;
import java.util.*;

public class Semantic {

    private List<Integer> errorLines;

    private StringBuilder errorInfo;

    private int toneOffset;

    private int haftToneOffset;

    private Map<String, Paragraph> paragraphMap;

    private MidiTrackBuilder midiTrackBuilder= new MidiTrackBuilder();

    private MidiFileBuilder midiFileBuilder= new MidiFileBuilder();

    private MidiFile midiFile;

    private List<MidiTrack> midiTracks;

    public String interpret(Node abstractSyntaxTree) {

        errorLines = new ArrayList<>();
        errorInfo = new StringBuilder();

        toneOffset = 0;
        haftToneOffset = 0;

        paragraphMap = new HashMap<>();

        midiTracks = new ArrayList<>();

        processTreeNode(abstractSyntaxTree, null);

        if (getIsError())
            return null;

        midiFile = midiFileBuilder.createMidiFile(midiTracks).constructMidiFile().getCurrentMidiFile();

        return midiFile.toString();
    }

    private void processTreeNode(Node curNode, Paragraph para) {
        Paragraph paragraph = para;
        List<Integer> noteList;
        List<Integer> durationList;
        int lineNoteCount = 0;
        int lineRhythmCount = 0;

        for (Node child : curNode.getChildren()) {
            switch (child.getType()) {
                case "score":
                    processTreeNode(child, paragraph);
                    break;

                case "execution":
                    processTreeNode(child, paragraph);
                    break;

                case "statement":
                    if (paragraphMap.containsKey(child.getChild(0).getContent())) {
                        errorInfo.append("Line: ").append(child.getChild(0).getLine()).append("\t重复声明的段落名").append(child.getChild(0).getContent()).append("\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    paragraph = new Paragraph();
                    paragraphMap.put(child.getChild(0).getContent(), paragraph);
                    break;

                case "instrument":
                    if (child.getChild(0).getContent().length() < 4 && Integer.parseInt(child.getChild(0).getContent()) >= -1 && Integer.parseInt(child.getChild(0).getContent()) < 128) {
                        paragraph.setInstrument(Integer.parseInt(child.getChild(0).getContent()));
                    } else {
                        errorInfo.append("Line: ").append(child.getChild(0).getLine()).append("\t乐器声明超出范围（-1~127）\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    break;

                case "volume":
                    if (child.getChild(0).getContent().length() < 4 && Integer.parseInt(child.getChild(0).getContent()) >= 0 && Integer.parseInt(child.getChild(0).getContent()) < 128) {
                        paragraph.setVolume(Byte.parseByte(child.getChild(0).getContent()));
                    } else {
                        errorInfo.append("Line: ").append(child.getChild(0).getLine()).append("\t音量声明超出范围（0~127）\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    break;

                case "speed":
                    if (Float.parseFloat(child.getChild(0).getContent()) >= 0 && Float.parseFloat(child.getChild(0).getContent()) < 1000) {
                        paragraph.setSpeed(Float.parseFloat(child.getChild(0).getContent()));
                    } else {
                        errorInfo.append("Line: ").append(child.getChild(0).getLine()).append("\t速度声明超出范围（0~999）\n");
                        errorLines.add(child.getChild(0).getLine());
                    }
                    break;

                case "tonality":
                    toneOffset = 0;
                    for (Node tonality : child.getChildren()) {
                        switch (tonality.getContent()) {
                            case "#":
                                toneOffset += 1;
                                break;
                            case "b":
                                toneOffset -= 1;
                                break;
                            case "C":
                                break;
                            case "D":
                                toneOffset += 2;
                                break;
                            case "E":
                                toneOffset += 4;
                                break;
                            case "F":
                                toneOffset += 5;
                                break;
                            case "G":
                                toneOffset += 7;
                                break;
                            case "A":
                                toneOffset -= 3;
                                break;
                            case "B":
                                toneOffset -= 1;
                                break;
                        }
                    }
                    break;

                case "sentence":
                    processTreeNode(child, paragraph);
                    break;

                case "end paragraph":
                    break;

                case "melody":
                    noteList = paragraph.getNoteList();
                    for (Node tone : child.getChildren()) {
                        switch (tone.getContent()) {
                            case "(":
                                toneOffset -= 12;
                                break;
                            case ")":
                                toneOffset += 12;
                                break;
                            case "[":
                                toneOffset += 12;
                                break;
                            case "]":
                                toneOffset -= 12;
                                break;
                            case "|":
                                paragraph.getSymbolQueue().offer(new Symbol(1, noteList.size()));
                                break;
                            case "#":
                                haftToneOffset = 1;
                                break;
                            case "b":
                                haftToneOffset = -1;
                                break;
                            case "0":
                                lineNoteCount++;
                                noteList.add(0);
                                break;
                            case "1":
                                lineNoteCount++;
                                noteList.add(60 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "2":
                                lineNoteCount++;
                                noteList.add(62 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "3":
                                lineNoteCount++;
                                noteList.add(64 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "4":
                                lineNoteCount++;
                                noteList.add(65 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "5":
                                lineNoteCount++;
                                noteList.add(67 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "6":
                                lineNoteCount++;
                                noteList.add(69 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                            case "7":
                                lineNoteCount++;
                                noteList.add(71 + haftToneOffset + toneOffset);
                                haftToneOffset = 0;
                                break;
                        }
                    }
                    break;

                case "rhythm":
                    durationList = paragraph.getDurationList();
                    Integer line = child.getChild(0).getLine();
                    for (Node rhythm : child.getChildren()) {
                        switch (rhythm.getContent()) {
                            case "{":
                                paragraph.getSymbolQueue().offer(new Symbol(0, durationList.size()));
                                break;
                            case "}":
                                paragraph.getSymbolQueue().offer(new Symbol(2, durationList.size()));
                                break;
                            case "1":
                                lineRhythmCount++;
                                durationList.add(480);
                                break;
                            case "1*":
                                lineRhythmCount++;
                                durationList.add(720);
                                break;
                            case "2":
                                lineRhythmCount++;
                                durationList.add(240);
                                break;
                            case "2*":
                                lineRhythmCount++;
                                durationList.add(360);
                                break;
                            case "4":
                                lineRhythmCount++;
                                durationList.add(120);
                                break;
                            case "4*":
                                lineRhythmCount++;
                                durationList.add(180);
                                break;
                            case "8":
                                lineRhythmCount++;
                                durationList.add(60);
                                break;
                            case "8*":
                                lineRhythmCount++;
                                durationList.add(90);
                                break;
                            case "g":
                                lineRhythmCount++;
                                durationList.add(30);
                                break;
                            case "g*":
                                lineRhythmCount++;
                                durationList.add(45);
                                break;
                            case "w":
                                lineRhythmCount++;
                                durationList.add(15);
                                break;
                            case "w*":
                                lineRhythmCount++;
                                errorInfo.append("Line: ").append(line).append("\t不支持32分附点音符，即w*\n");
                                errorLines.add(line);
                                break;
                        }
                    }

                    if (lineNoteCount != lineRhythmCount) {
                        errorInfo.append("Line: ").append(line).append("\t该句音符与时值数量不相同\n");
                        errorLines.add(line);
                    }

                    break;

                case "playlist":
                    String paraName = "";
                    int index = 0;
                    int totalDuration = 0;

                    for (Node playList : child.getChildren()) {
                        switch (playList.getContent()) {
                            case "&":
                                index++;
                                if (index > 15 || index == 9)
                                    index = ++index % 16;
                                break;

                            case ",":
                                if (!paragraphMap.containsKey(paraName))
                                    break;

                                index = 0;

                                List<Integer> duration = paragraphMap.get(paraName).getDurationList();

                                for (int dura : duration)
                                    totalDuration += dura;

                                break;

                            default:
                                paraName = playList.getContent();

                                if (!paragraphMap.containsKey(paraName)) {
                                    errorInfo.append("Line: ").append(playList.getLine()).append("\t未声明的段落名").append(paraName).append("\n");
                                    errorLines.add(playList.getLine());
                                    break;
                                }

                                int tempIndex = index;

                                if (paragraphMap.get(paraName).getInstrument() == -1) {
                                    paragraphMap.get(paraName).setInstrument(0);
                                    index = 9;
                                }

                                MidiTrack midiTrack;

                                if (index > midiTracks.size() - 1) {
                                    midiTrack = constuctMidiTrackPart(paragraphMap.get(paraName), totalDuration, (byte) index);
                                    if (midiTrack != null)
                                        midiTracks.add(midiTrack);
                                } else {
                                    midiTrack = constuctMidiTrackPart(paragraphMap.get(paraName), 0, (byte) index);
                                    if (midiTrack != null)
                                        midiTrackBuilder.merge(midiTracks.get(index), midiTrack);
                                }

                                index = tempIndex;

                                break;
                        }
                    }

                    if (!getIsError())
                        for (MidiTrack midiTrack : midiTracks)
                            midiTrackBuilder.setEnd(midiTrack);
            }
        }
    }

    private MidiTrack constuctMidiTrackPart(Paragraph paragraph, int duration, byte channel) {
        if (getIsError())
            return null;

        midiTrackBuilder.createMidiTrack()
                .setStart()
                .setBpm(paragraph.getSpeed())
                .setInstrument(channel, (byte) paragraph.getInstrument())
                .addController(channel, (byte) 0x07, paragraph.getVolume());

        if (duration != 0)
            midiTrackBuilder.setDuration(duration);

        List<Integer> noteList = paragraph.getNoteList();
        List<Integer> durationList = paragraph.getDurationList();

        Queue<Note> bufferNotes = new PriorityQueue<>(Comparator.comparingInt(Note::getDeltaTime));

        Queue<Symbol> symbolQueue = paragraph.getSymbolQueue();

        Note tempNote;

        int count = noteList.size();
        for (int index = 0; index < count; ++index) {
            while (!symbolQueue.isEmpty() && symbolQueue.peek().getPosition() == index) {
                //处理特殊符号，i为符号后一个音符
                switch (symbolQueue.poll().getSymbol()) {
                    case 1:
                        //同时音
                        if (!symbolQueue.isEmpty()) {
                            if (symbolQueue.peek().getSymbol() != 1) {
                                errorInfo.append("Line: 未知\t同时音间存在连音无意义\n");
                                errorLines.add(0);
                                break;
                            }

                            for (boolean isPrimary = true; true; isPrimary = false) {
                                midiTrackBuilder.insertNoteOn(channel, noteList.get(index).byteValue(), (byte) 80);
                                tempNote = new Note(durationList.get(index), noteList.get(index++).byteValue(), isPrimary);
                                bufferNotes.offer(tempNote);

                                if (symbolQueue.peek().getPosition() == index)
                                    break;
                            }

                            symbolQueue.poll();
                            do {
                                tempNote = bufferNotes.poll();
                                midiTrackBuilder.insertNoteOff(tempNote.getDeltaTime(), channel, tempNote.getNote());
                                reduceDeltaTimeInQueue(bufferNotes, tempNote.getDeltaTime());
                            }
                            while (!bufferNotes.isEmpty() && (!tempNote.getIsPrimary() || bufferNotes.peek().getDeltaTime() == 0));
                        }
                        break;

                    case 0:
                        //连音
                        if (!symbolQueue.isEmpty()) {
                            if (symbolQueue.peek().getSymbol() != 2) {
                                errorInfo.append("Line: 未知\t连音间存在同时音无意义\n");
                                errorLines.add(0);
                                break;
                            }

                            byte currentNote;
                            byte lastNote = -1;
                            int totalDuration = 0;

                            do {
                                currentNote = noteList.get(index).byteValue();
                                if (currentNote != lastNote) {
                                    if (lastNote != -1) {
                                        midiTrackBuilder.insertNoteOn(channel, lastNote, (byte) 80);
                                        midiTrackBuilder.insertNoteOff(totalDuration, channel, lastNote);
                                        totalDuration = 0;
                                    }
                                    lastNote = noteList.get(index).byteValue();
                                }
                                totalDuration += durationList.get(index);
                                index++;
                            } while (symbolQueue.peek().getPosition() != index);

                            symbolQueue.poll();

                            midiTrackBuilder.insertNoteOn(channel, lastNote, (byte) 80);
                            midiTrackBuilder.insertNoteOff(totalDuration, channel, lastNote);
                        }
                        break;
                }
            }

            if (index < count) {
                //特殊符号外的音
                midiTrackBuilder.insertNoteOn(channel, noteList.get(index).byteValue(), (byte) 80);

                if (!bufferNotes.isEmpty()) {
                    //还有同时音在播放中
                    while (!bufferNotes.isEmpty() && durationList.get(index) >= bufferNotes.peek().getDeltaTime()) {
                        tempNote = bufferNotes.poll();
                        midiTrackBuilder.insertNoteOff(tempNote.getDeltaTime(), channel, tempNote.getNote());
                        reduceDeltaTimeInQueue(bufferNotes, tempNote.getDeltaTime());
                        durationList.set(index, durationList.get(index) - tempNote.getDeltaTime());
                    }
                    reduceDeltaTimeInQueue(bufferNotes, durationList.get(index));
                }

                midiTrackBuilder.insertNoteOff(durationList.get(index), channel, noteList.get(index).byteValue());
            }
        }

        while (!bufferNotes.isEmpty()) {
            //还有同时音在播放中
            tempNote = bufferNotes.poll();
            midiTrackBuilder.insertNoteOff(tempNote.getDeltaTime(), channel, tempNote.getNote());
            reduceDeltaTimeInQueue(bufferNotes, tempNote.getDeltaTime());
        }

        return midiTrackBuilder.getCurrentMidiTrack();
    }

    private void reduceDeltaTimeInQueue(Queue<Note> bufferNotes, int deltaTime) {
        for (Note noteInQueue : bufferNotes)
            noteInQueue.setDeltaTime(noteInQueue.getDeltaTime() - deltaTime);
    }

    public boolean getIsError() {
        return !errorLines.isEmpty();
    }

    public List<Integer> getErrorLines() {
        return errorLines;
    }

    public String getErrors() {
        return errorInfo.toString();
    }

    public MidiFile getMidiFile() {
        return midiFile;
    }

}