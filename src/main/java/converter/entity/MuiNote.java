package converter.entity;

public class MuiNote {
    private int pitch;//音高
    private String timeString;//1248gw
    private int noteNumbers;//有几个音符
    private double durationTicks;//持续时间240
    private long triggerTick;//开始时间
    private int channelNumber;//通道编号
    private int resolution;//分辨率480
    private int trackNumber;//音轨编号

    public MuiNote(int pitch, String timeString, int noteNumbers, double durationTicks) {
        this.pitch = pitch;
        this.timeString = timeString;
        this.noteNumbers = noteNumbers;
        this.durationTicks = durationTicks;
    }

    public MuiNote(int pitch, String timeString, int noteNumbers, double durationTicks, long triggerTick, int channelNumber, int resolution,int trackNumber) {
        this.pitch = pitch;
        this.timeString = timeString;
        this.noteNumbers = noteNumbers;
        this.durationTicks = durationTicks;
        this.triggerTick = triggerTick;
        this.channelNumber = channelNumber;
        this.resolution = resolution;
        this.trackNumber=trackNumber;
    }

    public String getPitchString() {
        StringBuilder pitchString=new StringBuilder();
        int smallBracket=0;
        int middleBracket=0;
        String note=null;
        if(pitch==-1){
            note="0";
            for(int i=0;i<noteNumbers;++i)
                pitchString.append(note);
            return pitchString.toString();
        }
        while(pitch>71){
            pitch-=12;
            ++middleBracket;
        }
        while(pitch<60){
            pitch+=12;
            ++smallBracket;
        }
        switch (pitch){
            case 60:
                note="1";
                break;
            case 61:
                note="#1";
                break;
            case 62:
                note="2";
                break;
            case 63:
                note="#2";
                break;
            case 64:
                note="3";
                break;
            case 65:
                note="4";
                break;
            case 66:
                note="#4";
                break;
            case 67:
                note="5";
                break;
            case 68:
                note="#5";
                break;
            case 69:
                note="6";
                break;
            case 70:
                note="#6";
                break;
            case 71:
                note="7";
                break;
        }
        for(int i=0;i<smallBracket;i++)
            pitchString.append("(");
        for(int i=0;i<middleBracket;i++)
            pitchString.append("[");
        for(int i=0;i<noteNumbers;i++)
            pitchString.append(note);
        for(int i=0;i<smallBracket;i++)
            pitchString.append(")");
        for(int i=0;i<middleBracket;i++)
            pitchString.append("]");

        if(noteNumbers==0){
            pitchString.delete(0,pitchString.length());
        }

        return pitchString.toString();
    }

    public String getTimeString() {
        if(noteNumbers>1){
            StringBuilder newTimeString=new StringBuilder("{");
            newTimeString.append(timeString).append("}");
            return newTimeString.toString();
        }
        return timeString;
    }

    public int getNoteNumbers() {
        return noteNumbers;
    }

    public double getDurationTicks() {
        return durationTicks;
    }

    public MuiNote getStandardMuiNote(int resolution){
        if(durationTicks>=5*resolution)
            return new MuiNote(pitch,"1*",1,6*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=3.5*resolution)
            return new MuiNote(pitch,"1",1,4*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=2.5*resolution)
            return new MuiNote(pitch,"2*",1,3*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=1.75*resolution)
            return new MuiNote(pitch,"2",1,2*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=1.25*resolution)
            return new MuiNote(pitch,"4*",1,1.5*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=0.875*resolution)
            return new MuiNote(pitch,"4",1,resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=0.625*resolution)
            return new MuiNote(pitch,"8*",1,0.75*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=0.4375*resolution)
            return new MuiNote(pitch,"8",1,0.5*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=0.3125*resolution)
            return new MuiNote(pitch,"g*",1,0.375*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else if(durationTicks>=0.1875*resolution)
            return new MuiNote(pitch,"g",1,0.25*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);
        else
            return new MuiNote(pitch,"w",1,0.125*resolution,this.triggerTick,this.channelNumber,this.resolution,this.trackNumber);

    }


}
