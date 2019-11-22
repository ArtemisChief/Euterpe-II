package entity.gui;

public enum Status {

    NEW_EMPTY, NEW_TEMPLATE, SAVED, EDITED, PLAYING;

    private static Status CurrentStatus = NEW_EMPTY;

    public static Status GetCurrentStatus(){
        return CurrentStatus;
    }

    public static void SetCurrentStatus(Status status){
        CurrentStatus = status;
    }

}
