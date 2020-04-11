package interpreter.entity;

import java.util.*;

/**
 * 段落类
 * 用于语义分析构造符号表
 * 以便翻译成Midi语言
 */

public class Paragraph {

    private Float Speed;

    private byte volume;

    private int instrument;

    private final List<Integer> noteList;

    private final List<Integer> durationList;

    private final Queue<Symbol> symbolQueue;

    public Paragraph() {
        Speed = 0.0F;
        noteList = new ArrayList<>();
        durationList = new ArrayList<>();
        symbolQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPosition() == o2.getPosition())
                return o2.getSymbol() - o1.getSymbol();
            else
                return o1.getPosition() - o2.getPosition();
        });
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public Float getSpeed() {
        return Speed;
    }

    public void setSpeed(Float speed) {
        Speed = speed;
    }

    public List<Integer> getNoteList() {
        return noteList;
    }

    public List<Integer> getDurationList() {
        return durationList;
    }

    public Queue<Symbol> getSymbolQueue() {
        return symbolQueue;
    }

}