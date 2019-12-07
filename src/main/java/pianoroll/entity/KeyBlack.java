package pianoroll.entity;

public class KeyBlack extends Key{

    private static final float[] vertexData = {
            -0.65f,  0.0f,          // Left-Top
            -0.65f, -8.0f,          // Left-Bottom
             0.65f,  0.0f,          // Right-Top
             0.65f, -8.0f           // Right-Bottom

    };

    public KeyBlack(int trackID) {
        super(trackID, 201);
    }

    @Override
    public void press() {
        super.press();
        super.setColorID(getTrackID());
    }

    @Override
    public void release() {
        super.release();
        super.setColorID(201);
    }

    public static float[] GetVertexData() {
        return vertexData;
    }

}
