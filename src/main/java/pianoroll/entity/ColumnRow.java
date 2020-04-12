package pianoroll.entity;

public class ColumnRow extends GraphicElement {

    private float offsetY;

    public ColumnRow(int trackID, float offsetY) {
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
