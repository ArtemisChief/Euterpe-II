package converter.entity;

public class NmnNote{
    //基本信息
    private int pitch;//音高
    private int time;//音符时值1、2、4、8、16、32
    private int dotNum;


    public NmnNote(int pitch, int time, int dotNum){
        this.pitch = pitch;
        this.time = time;
        this.dotNum = dotNum;
    }
    public void setPitch(int pitch){
        this.pitch = pitch;
    }

    public int getPitch(){
        return pitch;
    }
    public int getTime(){
        return time;
    }
    public int getDotNum(){
        return dotNum;
    }
}
