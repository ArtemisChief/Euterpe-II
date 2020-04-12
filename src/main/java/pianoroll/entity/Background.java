package pianoroll.entity;

public class Background extends GraphicElement {

    private float offsetY;

    public Background(int trackID, float offsetY) {
        super(trackID);

        this.offsetY = offsetY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

}
