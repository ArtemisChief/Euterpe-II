package pianoroll.entity;

public class Roll extends GraphicElement {

    private float offsetY;

    private float scaleY;

    private boolean isValid;
    private boolean isTriggered;

    public Roll(int trackID, float offsetY, float scaleY) {
        super(trackID);

        setColorID(trackID);

        this.offsetY = offsetY;
        this.scaleY = scaleY;

        isValid =true;
        isTriggered=false;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setTriggered(boolean triggered) {
        isTriggered = triggered;
    }

}