package interpreter.entity;

/**
 * 事件类
 * 用于记录事件及其位置
 * 以便对事件进行处理
 *
 * 0    速度事件
 * 1    音量事件
 * 2    乐器事件
 */

public class Event {

    private final int type;

    private final String data;

    private final int position;

    public Event(int type,String data, int position) {
        this.type = type;
        this.data = data;
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }

}
