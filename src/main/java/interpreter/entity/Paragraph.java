package interpreter.entity;

import java.util.*;

/**
 * 段落类
 * 用于语义分析构造符号表
 * 以便翻译成Midi语言
 */

public class Paragraph {

    private final List<Integer> noteList;

    private final List<Integer> durationList;

    private final Queue<Event> eventQueue;

    private final Queue<Symbol> symbolQueue;

    public Paragraph() {
        noteList = new ArrayList<>();
        durationList = new ArrayList<>();
        eventQueue=new LinkedList<>();
        symbolQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getPosition() == o2.getPosition())
                return o2.getSymbol() - o1.getSymbol();
            else
                return o1.getPosition() - o2.getPosition();
        });
    }

    public List<Integer> getNoteList() {
        return noteList;
    }

    public List<Integer> getDurationList() {
        return durationList;
    }

    public Queue<Event> getEventQueue() {
        return eventQueue;
    }

    public Queue<Symbol> getSymbolQueue() {
        return symbolQueue;
    }

}