package pianoroll.entity;

public abstract class Roll extends GraphicElement{

    private float offsetY;

    private float scaleY;

    boolean isUpdatingScaleY;

    public Roll(int trackID, int colorID) {
        super(trackID, colorID);

        offsetY = 0.0f;
        scaleY = 1.0f;
    }

    public void update(float deltaY) {
        offsetY += deltaY;

        if (isUpdatingScaleY)
            scaleY += deltaY;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setUpdatingScaleY(boolean updatingScaleY) {
        isUpdatingScaleY = updatingScaleY;
    }

}