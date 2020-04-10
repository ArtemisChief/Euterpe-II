package pianoroll.entity;

public class ColumnRow extends GraphicElement {

    private float offsetY;

    public ColumnRow(int trackID) {
        super(trackID);
        offsetY = 0.0f;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

}
