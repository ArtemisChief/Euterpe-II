package gui.entity;

public enum Status {

    NEW_FILE(), SAVED_FILE();

    private boolean isEdited;

    Status() {
        this.isEdited = false;
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
