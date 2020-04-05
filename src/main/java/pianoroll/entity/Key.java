package pianoroll.entity;

public class Key extends GraphicElement {

    private final int pitch;

    public Key(int trackID) {
        super(trackID);

        if (IsWhite(trackID))
            setColorID(200);
        else
            setColorID(201);

        this.pitch = trackID + 21;
    }

    public int getPitch() {
        return pitch;
    }

}