package entity.gui;

public enum Status {

    NEW_FILE(false), SAVED_FILE(false);

    private boolean isEdited;

    Status(boolean isEdited) {
        this.isEdited = isEdited;
    }

    private static Status CurrentStatus = NEW_FILE;

    public static Status GetCurrentStatus() {
        return CurrentStatus;
    }

    public static void SetCurrentStatus(Status status) {
        status.isEdited=false;
        CurrentStatus = status;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

}
