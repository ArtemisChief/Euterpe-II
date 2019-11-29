package pianoroll.entity;

public abstract class Roll {

    private int keyID;

    private int colorID;

    public Roll(int keyID, int colorID) {
        this.keyID = keyID;
        this.colorID = colorID;
    }

    public int getKeyID() {
        return keyID;
    }

    public int getColorID() {
        return colorID;
    }

    protected void setColorID(int colorID) {
        this.colorID = colorID;
    }

}