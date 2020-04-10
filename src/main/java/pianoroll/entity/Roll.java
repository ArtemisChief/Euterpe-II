package pianoroll.entity;

public class Roll extends GraphicElement {

    private float offsetY;

    private float scaleY;

    boolean isUnused;

    public Roll() {
        super(-1);

        offsetY = 0.0f;
        scaleY = 1.0f;

        isUnused = true;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isUnused() {
        return isUnused;
    }

    public void setUnused(boolean unused) {
        isUnused = unused;
    }

}